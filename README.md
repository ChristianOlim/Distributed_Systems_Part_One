# Distributed_Systems_Part_One - G00334621

# Overview 
Storing user passwords in plain text is poor security practice. Best 
practice is to store a salted hash of the user’s password, along with the 
salt used to generate the hash. When a user attempts to authenticate with
their password, a system can check if the password is
valid by generating a hash of the password along with the salt, and 
comparing it with the stored hash. See 
https://crackstation.net/hashing-security.htm for more details
on password hashing.
In Part 1 you’ll develop a Password Service which will provide the password hashing and verification services described above.
Your service will expose a gRPC API with two
methods:

• hash: Used to generate a hash of a user’s password. Takes a password as input,
returns the hash of the password, along with the salt used to generate the hash.
Includes userId on input and output because we will be calling the method asynchronously in Part 2 and will need the userId on the asynchronous response.
• validate: Used to validate a user-entered password by comparing it to the stored
hash. Takes a password, a hashed password and a salt as input. Uses the salt to
hash the input password and compares the resulting hash to the hashed password.

The userId and password input parameters to the hash and validate methods should
be of type integer and string respectively. You are free to determine the other types as you see fit
