// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player.social;

import java.util.Iterator;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.qwq.Friends;
import java.util.ArrayList;

public class SocialManager
{
    private static final ArrayList<Friend> friends;
    private static final ArrayList<Enemy> enemies;
    private static final ArrayList<Ignore> ignores;
    
    public static ArrayList<Friend> getFriends() {
        return SocialManager.friends;
    }
    
    public static ArrayList<Enemy> getEnemies() {
        return SocialManager.enemies;
    }
    
    public static ArrayList<Ignore> getIgnores() {
        return SocialManager.ignores;
    }
    
    public static ArrayList<String> getFriendsByName() {
        final ArrayList<String> friendNames = new ArrayList<String>();
        getFriends().forEach(friend -> friendNames.add(friend.getName()));
        return friendNames;
    }
    
    public static ArrayList<String> getEnemiesByName() {
        final ArrayList<String> enemyNames = new ArrayList<String>();
        getEnemies().forEach(enemy -> enemyNames.add(enemy.getName()));
        return enemyNames;
    }
    
    public static ArrayList<String> getIgnoresByName() {
        final ArrayList<String> ignoreNames = new ArrayList<String>();
        getIgnores().forEach(ignore -> ignoreNames.add(ignore.getName()));
        return ignoreNames;
    }
    
    public static boolean isFriend(final String name) {
        for (final Friend friend : getFriends()) {
            if (friend.getName().equalsIgnoreCase(name) && ModuleManager.isModuleEnabled(Friends.class)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isOnFriendList(final String name) {
        boolean value = false;
        for (final Friend friend : getFriends()) {
            if (friend.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }
        return value;
    }
    
    public static boolean isEnemy(final String name) {
        for (final Enemy enemy : getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isOnEnemyList(final String name) {
        boolean value = false;
        for (final Enemy enemy : getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }
        return value;
    }
    
    public static boolean isIgnore(final String name) {
        for (final Ignore ignore : getIgnores()) {
            if (ignore.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isOnIgnoreList(final String name) {
        boolean value = false;
        for (final Ignore ignore : getIgnores()) {
            if (ignore.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }
        return value;
    }
    
    public static Friend getFriend(final String name) {
        for (final Friend friend : getFriends()) {
            if (friend.getName().equalsIgnoreCase(name)) {
                return friend;
            }
        }
        return null;
    }
    
    public static Enemy getEnemy(final String name) {
        for (final Enemy enemy : getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                return enemy;
            }
        }
        return null;
    }
    
    public static Ignore getIgnore(final String name) {
        for (final Ignore ignore : getIgnores()) {
            if (ignore.getName().equalsIgnoreCase(name)) {
                return ignore;
            }
        }
        return null;
    }
    
    public static void addFriend(final String name) {
        if (!isOnFriendList(name)) {
            getFriends().add(new Friend(name));
        }
    }
    
    public static void delFriend(final String name) {
        getFriends().remove(getFriend(name));
    }
    
    public static void addEnemy(final String name) {
        if (!isOnEnemyList(name)) {
            getEnemies().add(new Enemy(name));
        }
    }
    
    public static void delEnemy(final String name) {
        getEnemies().remove(getEnemy(name));
    }
    
    public static void addIgnore(final String name) {
        if (!isOnIgnoreList(name)) {
            getIgnores().add(new Ignore(name));
        }
    }
    
    public static void delIgnore(final String name) {
        getIgnores().remove(getIgnore(name));
    }
    
    public static void clearIgnoreList() {
        getIgnores().clear();
    }
    
    static {
        friends = new ArrayList<Friend>();
        enemies = new ArrayList<Enemy>();
        ignores = new ArrayList<Ignore>();
    }
}
