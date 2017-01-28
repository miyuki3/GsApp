//ListViewに１つのセルの情報(message_item.xmlとMessageRecord)を結びつけるためのクラス
package com.gashfara.miyuki.gsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//<MessageRecord>はデータクラスMessageRecordのArrayAdapterであることを示している。このアダプターで管理したいデータクラスを記述されば良い。
// MessageRecordsAdapterは自分で作った名前。extends ArrayAdapterとはArrayAdapterの機能をコピーしてMessageRecordsAdapterとして作りますよという意味。
//　<MessageRecord>とは、ArrayAdapterはデータクラス<MessageRecord>をくっつけますよという意味。
//　class自体が変数になる。
public class MessageRecordsAdapter extends ArrayAdapter<MessageRecord> {
    private ImageLoader mImageLoader;

    //アダプターを作成する関数。コンストラクター。クラス名と同じです。
    public MessageRecordsAdapter(Context context) {
        //レイアウトのidmessage_itemのViewを親クラスに設定している。Rとはreosurceのことで勝手に作られているクラス。
        super(context, R.layout.message_item);
        //キャッシュメモリを確保して画像を取得するクラスを作成。これを使って画像をダウンロードする。Volleyの機能
        mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }
    //表示するViewを返します。これがListVewの１つのセルとして表示されます。表示されるたびに実行されます。getViewにはViewをリサイクルする機能がある。それが View convertView,
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertViewをチェックし、Viewがないときは新しくViewを作成します。convertViewがセットされている時は未使用なのでそのまま再利用します。メモリーに優しい。
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        //レイアウトにある画像と文字のViewを所得します。
        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image1);
        TextView textView = (TextView) convertView.findViewById(R.id.text1);
        TextView textView1 =(TextView) convertView.findViewById(R.id.text2);

//        //--------  SpannableStringでwebリンクを制御するプログラムはここから（まだできてない）
//
//    private void setSpannableString(View view) {
//
//        String message = textView;
//
//        // リンク化対象の文字列、リンク先 URL を指定する
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("利用規約", "http://hogehoge.com/rules");
//        map.put("プライバシーポリシー", "http://hogehoge.com/policies");
//
//        // SpannableString の取得
//        SpannableString ss = createSpannableString(message, map);
//
//        // SpannableString をセットし、リンクを有効化する
//        TextView textView = (TextView) view.findViewById(R.id.text);
//        textView.setText(ss);
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//    }
//    private SpannableString createSpannableString(String textView, Map<String, String> map) {
//
//            SpannableString ss = new SpannableString(textView);
//
//            for (final Map.Entry<String, String> entry : map.entrySet()) {
//                int start = 0;
//                int end = 0;
//
//                // リンク化対象の文字列の start, end を算出する
//                Pattern pattern = Pattern.compile(entry.getKey());
//                Matcher matcher = pattern.matcher(textView);
//                while (matcher.find()) {
//                    start = matcher.start();
//                    end = matcher.end();
//                    break;
//                }
//
//                //********SpannableString にクリックイベント、パラメータをセットする
//                ss.setSpan(new ClickableSpan() {
//                    @Override
//                    public void onClick(View textView) {
//                        String url = entry.getValue();
//                        Uri uri = Uri.parse(url);
//                        Intent intent = new Intent(view.getContext(), WebActivity.class);
////                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        view.getContext().startActivity(intent);
//                    }
//                }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            }
//
//            return ss;
//        }
//        //--------  SpannableStringでwebリンクを制御するプログラムはここまで
//
        //webリンクを制御するプログラムはここから
        // ******TextView に LinkMovementMethod を登録します
        //******TextViewをタップした時のイベントリスナー（タップの状況を監視するクラス）を登録します。onTouchにタップした時の処理を記述します。buttonやほかのViewも同じように記述できます。
        textView.setOnTouchListener(new ViewGroup.OnTouchListener() {
            //タップした時の処理
            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                //タップしたのはTextViewなのでキャスト（型の変換）する
                TextView textView = (TextView) view;
                //リンクをタップした時に処理するクラスを作成。AndroidSDKにあるLinkMovementMethodを拡張しています。
                MutableLinkMovementMethod m = new MutableLinkMovementMethod();
                //MutableLinkMovementMethodのイベントリスナーをさらにセットしています。
                m.setOnUrlClickListener(new MutableLinkMovementMethod.OnUrlClickListener() {
                    //リンクをクリックした時の処理
                    public void onUrlClick(TextView v,Uri uri) {
                        Log.d("myurl",uri.toString());//デバッグログを出力します。
                        // Intent のインスタンスを取得する。view.getContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
                        Intent intent = new Intent(view.getContext(), WebActivity.class);

                        // 渡したいデータとキーを指定する。urlという名前でリンクの文字列を渡しています。
                        intent.putExtra("url", uri.toString());

                        // 遷移先の画面を呼び出す
                        view.getContext().startActivity(intent);
                    }

                });
                //ここからはMutableLinkMovementMethodを使うための処理なので毎回同じ感じ。
                //リンクのチェックを行うため一時的にsetする
                textView.setMovementMethod(m);
                boolean mt = m.onTouchEvent(textView, (Spannable) textView.getText(), event);
                //チェックが終わったので解除する しないと親view(listview)に行けない
                textView.setMovementMethod(null);
                //setMovementMethodを呼ぶとフォーカスがtrueになるのでfalseにする
                textView.setFocusable(false);
                //戻り値がtrueの場合は今のviewで処理、falseの場合は親view(ListView)で処理
                return mt;
            }


        });
        //webリンクを制御するプログラムはここまで


        //表示するセルの位置からデータをMessageRecordのデータを取得します。
        MessageRecord imageRecord = getItem(position);

        //mImageLoaderを使って画像をダウンロードし、Viewにセットします。
        imageView.setImageUrl(imageRecord.getImageUrl(), mImageLoader);
        //Viewに文字をセットします。
        textView.setText(imageRecord.getComment());
        textView1.setText(imageRecord.getPrice());

        //1つのセルのViewを返します。
        return convertView;
    }
    //データをセットしなおす関数
    public void setMessageRecords(List<MessageRecord> objects) {
        //ArrayAdapterを空にする。
        clear();
        //テータの数だけMessageRecordを追加します。
        for(MessageRecord object : objects) {
            add(object);
        }
        //データの変更を通知します。
        notifyDataSetChanged();
    }
}
