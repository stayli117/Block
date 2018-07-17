package net.people.lifecycle_android.block;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.ArrayList;

public class NoobChain {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5;
    private static MutableLiveData<ArrayList<Block>> data;

    public static void main(long high, String previousHash) {
        //add our blocks to the blockchain ArrayList:
        addBlock(new Block("Hi im the " + high + "block", previousHash));
    }

    public static LiveData<ArrayList<Block>> getBlockChain() {
        data = new MutableLiveData<>();
        data.postValue(blockchain);
        return data;
    }


    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

        }
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        if (isChainValid()) {

            if (blockchain.add(newBlock)) {
                data.postValue(blockchain);
                Log.e(Block.TAG, "addBlock: " + blockchain);
                main(blockchain.size() + 1, blockchain.get(blockchain.size() - 1).hash);
            }
        }

    }
}
