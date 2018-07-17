package net.people.lifecycle_android.block;


import android.util.Log;

import net.people.lifecycle_android.TypesConverter;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data; //our data will be a simple message.
    public long timeStamp; //as number of milliseconds since 1/1/1970.
    private int nonce;

    //Block Constructor.
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {

        return StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
    }

    public static final String TAG = "Block";

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        String target = StringUtil.getDificultyString(difficulty); //Create a string with difficulty * "0"
        String hash = this.hash.substring(0, difficulty);

        while (!hash.equals(target)) {
            nonce++;
            this.hash = calculateHash();
            hash = this.hash.substring(0, difficulty);
            Log.e(TAG, "mineBlock: " + nonce + " target:" + target + " hash:" + hash);
        }

    }

    private long hex2Long(String target) {
        byte[] targetHex = TypesConverter.hexToBytes(target);
        return TypesConverter.byteArray2long(targetHex);
    }
}
