Contribution to Denizen
-----------------------

Refer to the Denizen contribution guide: https://github.com/DenizenScript/Denizen/blob/dev/CONTRIBUTING.md

## Testing changes

The instructions below are targeted at the `Intellij IDEA` IDE, although the process should be similar for most IDEs.  
You will be using the `Minecraft Client` run configuration to run a Minecraft client with Clientizen from within your IDE; as the client that runs does not include authentication by default, Clientizen makes use of the `DevAuth` mod to enable that.  
Go into your IDE, select the `Minecraft Client` run configuration from the run menu at the top bar (if it's missing, restart your IDE or run the `ideaSyncTask` Gradle task), and press run.  
Note the run terminal opening below, where `DevAuth` will output a link for you to authenticate your Microsoft account.  
You should only need to do that once (and after the token expires, which seems to be about 90 days according to the `DevAuth` developer), after that you will be able to simply run the `Minecraft Client` run configuration to start a Minecraft client with Clientizen.

For more information regarding setup in specific IDEs, preferred settings, and generating & reading Minecraft sources, see [Fabric's docs](https://fabricmc.net/wiki/tutorial:setup#intellij_idea).
