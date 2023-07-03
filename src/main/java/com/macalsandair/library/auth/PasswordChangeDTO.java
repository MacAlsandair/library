package com.macalsandair.library.auth;

import java.nio.CharBuffer;

public class PasswordChangeDTO {
    private CharBuffer oldPassword;
    private CharBuffer newPassword;
    
    public CharBuffer getOldPassword() {
        return CharBuffer.wrap(oldPassword);
    }
    
    public void setOldPassword(CharBuffer oldPassword) {
        if (this.oldPassword != null) {
            while (this.oldPassword.hasRemaining()) {
                this.oldPassword.put((char) 0);
            }
        }

        this.oldPassword = oldPassword;
    }
    
    public CharBuffer getNewPassword() {
        return CharBuffer.wrap(newPassword);
    }
    
    public void setNewPassword(CharBuffer newPassword) {
        if (this.newPassword != null) {
            while (this.newPassword.hasRemaining()) {
                this.newPassword.put((char) 0);
            }
        }
        this.newPassword = newPassword;
    }
}
