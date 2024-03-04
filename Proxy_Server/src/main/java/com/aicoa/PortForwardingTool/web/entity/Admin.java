package com.aicoa.PortForwardingTool.web.entity;

/**
 * @author aicoa
 * @date 2024/3/3 0:44
 */
public class Admin {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getpasswd() {
        return passwd;
    }

    public void setpasswd(String passwd) {
        this.passwd = passwd;
    }

    private int id;
    private String username;
    private String passwd;

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
