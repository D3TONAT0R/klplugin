package com.d3t.klplugin.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.Bukkit;

import com.d3t.klplugin.FileUtil;
import com.d3t.klplugin.KLPlugin;

public class BackupHandler {
	
	public static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public static void CheckAndMakeBackup() {
		try {
			FileUtil file = FileUtil.createFromFile(new File(KLPlugin.INSTANCE.getDataFolder()+"/backup.txt"));
			LocalDateTime lastTimestamp = LocalDateTime.parse(file.GetString("LastBackupTimestamp"), timestampFormat);
			Duration timespan = Duration.between(lastTimestamp, LocalDateTime.now());
			if(Math.abs(timespan.toHours()) >= 23) {
				MakeBackup(file);
			} else {
				System.out.println(String.format("Skipping backup creation since the last backup is only %s hours old.", Math.abs(timespan.toHours())));
			}
		} catch(Exception e) {
			System.out.println("Failed to create selective backup!");
			e.printStackTrace();
		}
	}
	
	public static void MakeBackup(FileUtil file) {
		int num = file.GetInt("BackupNum");
		int spanB = file.GetInt("BackupSpan_B");
		int spanC = file.GetInt("BackupSpan_C");
		List<String> files = new ArrayList<String>();
		String[] filesA = file.GetArray("Backup_A_Files");
		String[] filesB = file.GetArray("Backup_B_Files");
		String[] filesC = file.GetArray("Backup_C_Files");
		for(String s : filesA) files.add(s); //Always include files from class A
		String type = "A";
		if(num % spanC == 0) {
			//Include B and C
			for(String s : filesB) files.add(s);
			for(String s : filesC) files.add(s);
			type = "C";
		} else if(num % spanB == 0) {
			//Include B only
			for(String s : filesB) files.add(s);
			type = "B";
		}
		String[] fileArray = new String[files.size()];
		fileArray = files.toArray(fileArray);
		if(CreateArchive(file, fileArray, type)) {
			num++;
			file.SetValue("BackupNum", num);
			file.SetValue("LastBackupTimestamp", LocalDateTime.now().format(timestampFormat));
			file.Save("", "backup.txt");
		}
	}
	
	public static boolean CreateArchive(FileUtil file, String[] regions, String type) {
		try {
			String pathToRegions = file.GetString("PathToRegions");
			String archiveName = LocalDateTime.now().format(timestampFormat)+"_"+type+".bak";
			archiveName = archiveName.replace("-", "");
			File f = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath()+"/backups/"+archiveName);
			FileOutputStream output = new FileOutputStream(f);
			ZipOutputStream zip = new ZipOutputStream(output);
			for(String r : regions) {
				File rf = new File(pathToRegions+"/"+r+".mca");
				FileInputStream input = new FileInputStream(rf);
				ZipEntry entry = new ZipEntry(rf.getName());
				zip.putNextEntry(entry);
	            byte[] bytes = new byte[1024];
	            int length;
	            while((length = input.read(bytes)) >= 0) {
	                zip.write(bytes, 0, length);
	            }
	            input.close();
			}
			zip.close();
			output.close();
			System.out.println(String.format("Backup of type '%s' created successfully!", type));
			return true;
		} catch(Exception e) {
			System.out.println("Failed to write backup archive!");
			e.printStackTrace();
			return false;
		}
	}
}
