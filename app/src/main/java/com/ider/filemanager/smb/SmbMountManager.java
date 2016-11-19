package com.ider.filemanager.smb;

import android.os.Environment;
import android.util.Log;

import com.ider.filemanager.CommandExec;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ider-eric on 2016/11/19.
 */

public class SmbMountManager {

    private boolean DEBUG = true;
    private String TAG = "SmbMountManager";
    private void LOG(String log) {
        Log.i(TAG, log);
    }

    private ISmb.ISmbMountListener onMountListener;
    private static final String SHELL_LOG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/etc/smb_log";

    public SmbMountManager(ISmb.ISmbMountListener onMountListener) {
        this.onMountListener = onMountListener;
    }

    /**
     * 判断指定host地址是否已经挂载
     * @param sharedPath 指定host的共享目录,如//192.168.1.100/share
     * @return 如果已挂载，返回挂载点，否则返回null
     */
    public String isMounted(String sharedPath) {
        ArrayList<String> mountMsg = CommandExec.execCommand("mount");
        if(mountMsg == null) {
            return null;
        }
        for (String mount : mountMsg) {
            String[] msgs = mount.split(" ");
            if(msgs.length < 3) {
                continue;
            }
            if(msgs[2].equals("cifs")) {
                if(msgs[0].equals(sharedPath)) {
                    return msgs[1];
                }
            }
        }
        return null;
    }


    /**
     * 挂载cifs
     * @param smbHost 远端设备IP
     * @param sharedFolder 共享的目录
     * @return 挂载点
     */
    public String mount(SmbHost smbHost, String sharedFolder) {
        String hostIp = smbHost.getHost();
        String username = smbHost.getUsername();
        String password = smbHost.getPassword();
        boolean isPublic = smbHost.isPublic();

        String smbPath = "//" + hostIp + "/" + sharedFolder;

        // check mount point fisrt, if mounted, return mount point
        if(isMounted(smbPath) != null) {
            return isMounted(smbPath);
        }

        String mountPoint = createMountPoint();
        if(mountPoint == null) {
            onMountListener.onMountFailed(ISmb.ISmbMountListener.MOUNT_FAILED_CANNOT_CREATE_MOUNTPOINT);
            return null;
        }

        String command;
        if (isPublic) {
            command = "busybox mount -t cifs -o iocharset=utf8,username=guest,uid=1000,gid=1015,file_mode=0755,dir_mode=0755,rw "
                    + smbPath + " " + mountPoint + " > " + SHELL_LOG_PATH + " 2>&1";
        } else {
            String user = username;
            String pass = password;
            if(username.contains(" ")) {
                StringBuilder userbuilder = new StringBuilder("\"");
                userbuilder.append(username);
                userbuilder.append("\"");
                user = userbuilder.toString();
            }
            if(password.contains(" ")) {
                StringBuilder passbuilder = new StringBuilder("\"");
                passbuilder.append(password);
                passbuilder.append("\"");
                pass = passbuilder.toString();
            }
            String commandFormat = "busybox mount -t cifs -o iocharset=utf8,username=%s,password=%s,uid=1000,gid=1015,file_mode=0755,dir_mode=0755,rw "
                    + smbPath + " " + mountPoint + " > " + SHELL_LOG_PATH + " 2>&1";
            command = String.format(commandFormat, user, pass);
        }
        LOG(command);
        CommandExec.execCommand(command);


        return null;
    }

    private String createMountPoint() {
        String currentTimeMills = String.valueOf(System.currentTimeMillis());
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String sdcardSmbPath = sdcardPath + File.separator + "smb_share";
        String mountPointPath = sdcardSmbPath + File.separator + currentTimeMills;
        File sdcardSmb = new File(sdcardSmbPath);

        if(!sdcardSmb.exists()) {
            if(!sdcardSmb.mkdir()) {
                return null;
            }
        }
        File mountPoint = new File(mountPointPath);
        if(!mountPoint.mkdir()) {
            return null;
        }
        return mountPointPath;

    }


}
