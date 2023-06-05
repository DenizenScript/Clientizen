Contribution to Denizen
-----------------------

Refer to the Denizen contribution guide: https://github.com/DenizenScript/Denizen/blob/dev/CONTRIBUTING.md

## Testing changes

You will be using the `runClient` task to run a Minecraft client with Clientizen from within the IDE.  
As the client that task runs does not include authentication by default, Clientizen makes use of the `DevAuth` mod to do that.  
All you need to do to set up `DevAuth` is create its config file, you will want to place it in `C:\Users\<User>\.devauth\config.toml`, with the following contents
```toml
defaultEnabled = true
defaultAccount = "main"

[accounts.main]
type = "microsoft"
```
This will enable `DevAuth` and make it use a Microsoft account by default.  

---
Now that you have `DevAuth` setup, you can go into your IDE, select `Minecraft Client` from the run menu at the top bar and press run.
Note the run terminal opening below, where `DevAuth` will output a link for you to authenticate your Microsoft account.  
You should only need to do that once (and after the token expires, which seems to be about 90 days according to the author).