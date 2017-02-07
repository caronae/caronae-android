package br.ufrj.caronae.firebase;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Luis on 11/30/2016.
 */
public class FirebaseUtils {

    public static void SubscribeToTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public static void UnsubscribeToTopic(String topic){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    public static String MyUserID(){
        return "799";
    }
}
