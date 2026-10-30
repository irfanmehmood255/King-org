const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validProfile(uid) {
  return {
    userId: uid,
    email: "alice@example.com",
    country: "United States",
    termsAccepted: true,
    emailVerified: false,
    humanVerified: false,
    humanVerificationMethod: "none",
    username: "",
    onboardingStage: "registered",
    createdAt: new Date(Date.now() - 1000),
    updatedAt: new Date(Date.now() - 1000),
  };
}

function validMessage(uid, messageId = "msg_1001", conversationId = "conv_1") {
  return {
    messageId,
    conversationId,
    ownerId: uid,
    senderName: "You",
    isFromMe: true,
    text: "Hello from Arena Chat!",
    timeLabel: "10:45 AM",
    deliveryStatus: "sent",
    messageType: "TEXT",
    isStarred: false,
    isForwarded: false,
    voiceDurationSeconds: 0,
    sequenceOrder: 1001,
    createdAt: new Date(Date.now() - 1000),
    updatedAt: new Date(Date.now() - 1000),
  };
}

test("Unauthenticated user: cannot read users profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID)));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's profile (PII isolation)", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set(validProfile(BOB_UID));
  });
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: cannot spoof userId on profile create", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).set(validProfile(BOB_UID)));
});

test("Authenticated user: cannot inject ghost fields on update", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID)));
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can reserve an available valid username and check availability", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("usernames").doc("john_doe").set({
      userId: ALICE_UID,
      username: "john_doe",
      displayUsername: "John_Doe",
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  // Bob can check if john_doe is taken via get()
  await assertSucceeds(bobDb.collection("usernames").doc("john_doe").get());
  // Bob cannot overwrite Alice's username
  await assertFails(
    bobDb.collection("usernames").doc("john_doe").set({
      userId: BOB_UID,
      username: "john_doe",
      displayUsername: "john_doe",
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Authenticated user: cannot reserve invalid short username", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("usernames").doc("ab").set({
      userId: ALICE_UID,
      username: "ab",
      displayUsername: "ab",
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Authenticated user: can create, read, update deliveryStatus/reaction, and delete own chat messages", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("messages").doc("msg_1001").set(validMessage(ALICE_UID, "msg_1001", "conv_1"))
  );
  await assertSucceeds(aliceDb.collection("messages").doc("msg_1001").get());
  await assertSucceeds(
    aliceDb.collection("messages").doc("msg_1001").update({
      deliveryStatus: "read",
      reactionEmoji: "❤️",
      isStarred: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertSucceeds(aliceDb.collection("messages").doc("msg_1001").delete());
});

test("Cross-user isolation: Bob cannot read or modify Alice's chat messages", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("messages").doc("msg_alice").set(validMessage(ALICE_UID, "msg_alice", "conv_1"));
  });
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("messages").doc("msg_alice").get());
  await assertFails(
    bobDb.collection("messages").doc("msg_alice").update({
      deliveryStatus: "read",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(bobDb.collection("messages").doc("msg_alice").delete());
});
