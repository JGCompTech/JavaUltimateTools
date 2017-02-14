# OSInfo
The OSInfo namespace includes many classes that return information about the current OS installation. This includes the following:
- Architecture (String and Int representation)
- Name (If OS is Windows, MacOSX, Linux or Solaris)

# OSInfo.Windows
The OSInfo.Windows namespace includes many classes that return information about the current Windows installation. This includes the following:
- Edition (String representation)
- Name (String, ExpandedString and Enum representation) Also contains methods that will return the current and pending Computer Name.
- Product Key (String representation)
- Service Pack (String and Int representation)
- User Info (Contains Registered Organization, Registered Owner, Logged In Username, and Current Domain Name)
- Version (String and Int representation) Includes Main, Major, Minor, Build,  Revision and Number (number is Major * 10 + Minor).

# HWInfo
The HWInfo namespace includes many classes that return information about the current computer hardware. This includes the following:
- BIOS (Release Date, Version and Vendor Name)
- Network (Internal IP Address, External IP Address and Connection Status)
- OEM (Vendor Name and Product Name)
- Processor (Name and Number Of Cores)
- RAM (Total Installed Ram Size)
- Storage (System Drive Total Size, System Drive Free Space)

# ComputerInfo
This class is an instantiated class that contains all the info in the OSInfo and HWInfo classes. It extends the HWInfo.Storage class to contain all reconized drives not just the system drive.

# SecurityTools
The SecurityTools class contains methods surrounding hashing and encryption. This includes the following:
- createSecureRandom() - Secure Random Number Generator
- getFileHash(HashType type, String fileName) - Generates a file hash of the supplied filename via the selected hash type (MD5, SHA1, SHA256, SHA384 and SHA512)
- saveToFile(String hash, String fileName) - Saves a hash to a text file.
- saveToFile(Key key, String fileName) - Saves a RSA key to a text file.
- readFromFile(String fileName) - Reads a hash or RSA key from a text file.
- CreateSalt(int size) - Creates a RNG salt for use in password hashing using the supplied length.
- CreateHash(String passwordToHash, String salt) - Creates a SHA512 password hash with the supplied password and salt.
- CheckHashesMatch(String enteredPassword, String databasePassword, String databaseSalt) - Checks if the supplied password matches the supplied database password and salt. This can be used to verifiy passwords for a login system.
- generateRSAKeyPair() - Generates a RSA key pair for use in encryption.
- generateKeyPair(boolean saveToFiles, String filename) - Generates a RSA key pair for use in encryption and saves to file.
- encrypt(PublicKey key, String plaintext) - Encrypts a string using a RSA public key. Returns byte array.
- decrypt(PrivateKey key, byte[] ciphertext) - Decrypts a string using a RSA private key. Returns byte array.
- encryptToString(PublicKey key, String plaintext) - Encrypts a string using a RSA public key. Returns string.
- decryptFromString(PrivateKey key, String ciphertext) - Decrypts a string using a RSA private key. Returns string.
- saveKeyPairToFile(KeyPair pair, String filename) - Saves RSA key pair to text file.
- readPublicKeyFromBytes(byte[] bytes) - Reads RSA public key from bytes.
- readPrivateKeyFromBytes(byte[] bytes) - Reads RSA public key from bytes.
- readPublicKeyFromFile(String fileName) - Reads RSA public key from text file.
- readPrivateKeyFromFile(String fileName) - Reads RSA private key from text file.
