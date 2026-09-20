import collection.MyHashMap;

import java.util.HashMap;
import java.util.Map;


public class Main {

    public static void main(String[] args) {

        MyHashMap <String,Integer>myHashMap = new MyHashMap<>();
        myHashMap.put("apple", 1);
        myHashMap.put("orange", 2);
        myHashMap.put("pear", 3);
        myHashMap.put("banana", 4);
        myHashMap.put("kiwi", 5);
        myHashMap.put("cherry", 6);
        myHashMap.put("pineapple", 7);
        myHashMap.put("pine", 8);
        myHashMap.put("pinle", 9);
        myHashMap.put("pineape", 10);
        myHashMap.put("peapple", 11);
        myHashMap.put("peanut", 12);





        System.out.println(myHashMap.get("apple"));
        System.out.println(myHashMap.get("orange"));
        System.out.println(myHashMap.get("pear"));

        myHashMap.remove("apple");

        System.out.println(myHashMap.get("apple"));
        System.out.println(myHashMap.get("orange"));
        System.out.println(myHashMap.get("pear"));

        myHashMap.put("apple", 4);
        System.out.println(myHashMap.get("apple"));
        System.out.println(myHashMap.get("orange"));

        System.out.println("size " + myHashMap.getSize());

        System.out.println("capacity " + myHashMap.getCapacity());

    }
}