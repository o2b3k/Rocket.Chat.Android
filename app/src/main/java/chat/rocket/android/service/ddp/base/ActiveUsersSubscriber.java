package chat.rocket.android.service.ddp.base;

import android.content.Context;
import io.realm.RealmObject;
import org.json.JSONException;
import org.json.JSONObject;

import chat.rocket.android.model.ddp.User;
import chat.rocket.android.realm_helper.RealmHelper;
import chat.rocket.android.service.DDPClientRef;

/**
 * "activeUsers" subscriber.
 */
public class ActiveUsersSubscriber extends AbstractBaseSubscriber {
  public ActiveUsersSubscriber(Context context, String hostname, RealmHelper realmHelper,
                               DDPClientRef ddpClientRef) {
    super(context, hostname, realmHelper, ddpClientRef);
  }

  @Override
  protected String getSubscriptionName() {
    return "activeUsers";
  }

  @Override
  protected String getSubscriptionCallbackName() {
    return "users";
  }

  @Override
  protected Class<? extends RealmObject> getModelClass() {
    return User.class;
  }

  @Override
  protected JSONObject customizeFieldJson(JSONObject json) throws JSONException {
    json = super.customizeFieldJson(json);

    // The user object may have some children without a proper primary key (ex.: settings)
    // Here we identify this and add a local key
    // Only happens here when the logged user receives its own data
    if (json.has("settings")) {
      final JSONObject settingsJson = json.getJSONObject("settings");
      settingsJson.put("id", json.getString("_id"));

      if (settingsJson.has("preferences")) {
        final JSONObject preferencesJson = settingsJson.getJSONObject("preferences");
        preferencesJson.put("id", json.getString("_id"));
      }
    }

    return json;
  }
}
