# Security Specification (`security_spec.md`)

## 1. Data Invariants
1. **User Profile Isolation (`/users/{userId}`)**:
   - Only an authenticated user (`request.auth != null`) whose `request.auth.uid == userId` can read (`get`, `list`), `create`, `update`, or `delete` their own `/users/{userId}` document.
   - Prevents PII leakage (`email`, `country`) across users.
   - `userId` and `createdAt` are immutable after document creation.
   - `onboardingStage` must be one of `['registered', 'email_verified', 'human_verified', 'username_set', 'completed']`.
   - `humanVerificationMethod` must be one of `['none', 'face_scan', 'fingerprint_scan']`.
2. **Unique Username Enforcement (`/usernames/{usernameId}`)**:
   - `usernameId` must match `^[a-z0-9_]{3,20}$`.
   - Any signed-in user can `get` `/usernames/{usernameId}` to check availability.
   - Only the authenticated owner (`incoming().userId == request.auth.uid`) can `create` a username document, and only when the document does not already exist (guaranteed by Firestore `create` semantics).
   - Only the existing owner (`resource.data.userId == request.auth.uid`) can `update` or `delete` their reserved username.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Profile Read**: `unauthDb.collection("users").doc("alice").get()` -> Reject
2. **Cross-User PII Read**: `bobDb.collection("users").doc("alice").get()` -> Reject
3. **Identity Spoofing on Create**: `aliceDb.collection("users").doc("alice").set({ userId: "bob", ... })` -> Reject
4. **Ghost Field Injection (Shadow Update)**: `aliceDb.collection("users").doc("alice").update({ isAdmin: true })` -> Reject
5. **Invalid Username Format in Registry**: `aliceDb.collection("usernames").doc("ab").set(...)` (too short) -> Reject
6. **Username Hijacking by Another User**: `bobDb.collection("usernames").doc("alice_name").update({ userId: "bob" })` -> Reject
7. **Immutable `createdAt` Tampering**: `aliceDb.collection("users").doc("alice").update({ createdAt: newTimestamp })` -> Reject
8. **Invalid Enum Value for `onboardingStage`**: `aliceDb.collection("users").doc("alice").update({ onboardingStage: "hacked" })` -> Reject
9. **Oversized String DoS**: `email` > 254 chars or `country` > 100 chars -> Reject
10. **Unscoped List Query on `/users`**: `aliceDb.collection("users").get()` without `where("userId", "==", "alice")` -> Reject
11. **Unscoped List Query on `/usernames`**: `aliceDb.collection("usernames").get()` without `where("userId", "==", "alice")` -> Reject
12. **Future Timestamp Spoofing**: `createdAt > request.time` -> Reject
