Contribution to Denizen
-----------------------

Refer to the Denizen contribution guide: https://github.com/DenizenScript/Denizen/blob/dev/CONTRIBUTING.md

## Testing changes

You will be using the `runClient` task to run a Minecraft client with Clientizen from within the IDE.  
As the client that task runs does not include authentication by default, Clientizen makes use of the `DevAuth` mod to do that.  
Go into your IDE, select `Clientizen [runClient]` from the run menu at the top bar and press run.
Note the run terminal opening below, where `DevAuth` will output a link for you to authenticate your Microsoft account.  
You should only need to do that once (and after the token expires, which seems to be about 90 days according to the author), after that you will be able to simply run the `runClient` task to start a Minecraft client with Clientizen.
