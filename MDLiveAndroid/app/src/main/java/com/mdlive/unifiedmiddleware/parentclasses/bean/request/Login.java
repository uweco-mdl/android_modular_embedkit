package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

public class Login {

private String email;
private String password;
public Login(String email,String password){
this.email = email;
this.password = password;
}
/**
*
* @return
* The email
*/
public String getEmail() {
return email;
}

/**
*
* @param email
* The email
*/
public void setEmail(String email) {
this.email = email;
}

/**
*
* @return
* The password
*/
public String getPassword() {
return password;
}

/**
*
* @param password
* The password
*/
public void setPassword(String password) {
this.password = password;
}

}
