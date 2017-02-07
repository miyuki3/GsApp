package com.gashfara.miyuki.gsapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;

public class FbUserActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_user);
        //    Activity activity = this.getActivity();
        Activity activity = FbUserActivity.this;

        //ソーシャル・ネットワークコネクターのインスタンスを socialConnect メソッドで生成します。
        // この際、 SocialNetwork.SOCIALNETWORK_CONNECTOR を引数で指定します。
        KiiSocialConnect connect = Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);

        //Bundle を作成し、ログインを行う対象のソーシャルネットワークを指定します。
        // ここでは Provider.FACEBOOK を指定しています。
        Bundle options = new Bundle();
        options.putParcelable(KiiSocialNetworkConnector.PROVIDER, KiiSocialNetworkConnector.Provider.FACEBOOK);

        //logIn メソッドを実行してログインを開始します。指定されたアカウントが新規の場合、
        // 必要に応じてログイン処理に先立ちユーザー作成処理も同時に行われます。このメソッドはノンブロッキングのため、
        // 処理結果はコールバックにて取得しています。
        connect.logIn(activity, options, new

                KiiSocialCallBack() {
                    @Override
                    public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser
                            user, Exception exception) {
                        if (exception != null) {
                            // Error handling
                            showAlert(R.string.operation_failed, exception.getLocalizedMessage(), null);
                            return;
                        }

                        //自動ログインのためにSharedPreferenceに保存。アプリのストレージ。参考：http://qiita.com/Yuki_Yamada/items/f8ea90a7538234add288
                        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                        pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                        // Intent のインスタンスを取得する。getApplicationContext()で自分のコンテキストを取得。遷移先のアクティビティーを.classで指定
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // 遷移先の画面を呼び出す
                        startActivity(intent);
                        //戻れないようにActivityを終了します。
                        finish();
                    }
                }

        );
    }


    //注意：さらに、以下の例のように Facebook 認証を完了するためのメソッドを onActivityResult に追加する必要があります。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KiiSocialNetworkConnector.REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR).respondAuthOnActivityResult(
                    requestCode,
                    resultCode,
                    data);
        }
    }

    //ダイアログを表示する
    void showAlert(int titleId, String message, AlertDialogFragment.AlertDialogListener listener) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(titleId, message, listener);
        newFragment.show(getFragmentManager(), "dialog");
    }
}


