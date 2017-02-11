//1つのセルにあるデータを保存するためのデータクラスです。
package com.gashfara.miyuki.gsapp;

public class MessageRecord {
    //保存するデータ全てを変数で定義します。privateにしておくと変数名をあとから変えられる。公開したい部分と隠したい部分を制御できるのがJava。
    //newて呼び出さないと、classは作られない。
    private String imageUrl;
    private String comment;
    private String price;
    private String id;

    //データを１つ作成する関数です。項目が増えたら増やしましょう。MessageRecordはコンストラクターという.
    public MessageRecord(String id, String imageUrl, String comment, String price) {
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.price = price;
        this.id = id;
    }
    //それぞれの項目を返す関数です。項目が増えたら増やしましょう。getするためのfuncion。
    public String getComment() {
        return comment;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getPrice(){ return price; }
    public String getId() {
        return id;
    }
}
