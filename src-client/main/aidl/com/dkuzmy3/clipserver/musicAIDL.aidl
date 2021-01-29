// musicAIDL.aidl
package com.dkuzmy3.clipserver;

// Declare any non-default types here with import statements

interface musicAIDL {       // interface with the methods that control the player
    void playMusic(int number);
    void pauseMusic(int number);
    void stopMusic(int number);
}
