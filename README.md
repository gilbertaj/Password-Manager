# PasswordManager

Instructions for Running the App


Use on an Emulator through Android Studio

Make to select an android phone that supports fingerprint authentication for your emulator. To
run it on the emulator you will have to do some setup first.

1. Open the command prompt in Windows or the terminal in OS X

2. change the directory to the platform tools directory with the Android SDK
( ex. “C:\Users\YOURUSERNAME\AppData\Local\Android\Sdk\platform-tools” )

3. Enter in “adb -e emu finger touch 1234” to simulate a fingerprint touch in the running emulator.
The “1234” at the end can be replaced with any positive integer for a different fingerprint.

Now we will setup the emulated phone. Start the emulator without running the app. You can also start
the emulator with the app and then immediately close out of the app.

1. Access the Phone Settings (labeled Settings)
2. Go to the Security Tab
3. Click on the Fingerprint tab
4. Follow the instructions to setup a registered fingerprint. When prompted for a fingerprint
execute the command prompt line.

Now you can run the app on the emulated phone just like you would a real phone. Just use the
command prompt or terminal to execute a fingerprint touch.
