package br.com.zenix.hungergames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import com.google.common.base.Preconditions;

import br.com.zenix.core.spigot.Core;
import br.com.zenix.hungergames.manager.Manager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class HungerGames extends Core {

	private static Manager manager;
	private boolean TEST_SERVER;

	public void onEnable() {
		super.onEnable();

		if (!isCorrectlyStarted())
			return;

		manager = new Manager(this);
	}

	public void onDisable() {
		super.onDisable();
	}

	public void onLoad() {
		super.onLoad();
		TEST_SERVER = getConfig().getBoolean("test_server");
		if (!TEST_SERVER) {
			getServer().unloadWorld("world", false);
			deleteDir(new File("world"));
			
			String worldName = "arena";
			File file = new File(worldName);
			deleteDir(file);

			String worldName2 = "arena_bk";
			File file2 = new File(worldName2);

			copyFolder(file2, file);

			for (World world : Bukkit.getWorlds()) {
				world.setThundering(false);
				world.setStorm(false);
				world.setAutoSave(false);
				world.setTime(0L);
			}
		}
	}
	public boolean copyFolder(File src, File dest) {
		try {
			if (src.isDirectory()) {
				if (!dest.exists())
					dest.mkdir();

				String files[] = src.list();

				for (String file : files) {
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);
					copyFolder(srcFile, destFile);
				}
			} else {
				InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0)
					out.write(buffer, 0, length);

				in.close();
				out.close();
			}
			return true;
		} catch (Exception e) {
			getManager().getLogger().error("Error when the plugin is trying to copy the path " + src.getAbsolutePath()
					+ " to " + dest.getAbsolutePath() + ".", e);
		}
		return false;
	}
	
	public boolean deleteFile(Path path) {
		Preconditions.checkNotNull(path);
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path file, IOException e) {
					return handleException(e);
				}

				private FileVisitResult handleException(IOException e) {
					e.printStackTrace();
					return FileVisitResult.TERMINATE;
				}

				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
					if (e != null)
						return handleException(e);
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
			return true;
		} catch (IOException e) {
			getManager().getLogger().error("Error when the plugin is trying to delete the path " + path, e);
			return false;
		}
	}
	public boolean TEST_SERVER() {
		return TEST_SERVER;
	}

	public static Manager getManager() {
		return manager;
	}

	public void loadChunks() {
		for (int x = 600; x < 600; x++) {
			for (int z = -600; z < 600; z++) {
				Chunk chunk = Bukkit.getWorld("world").getBlockAt(x, 64, z).getChunk();
				chunk.load(true);
			}
		}
	}

	public void deleteDir(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(file, children[i]));
			}
		}
		file.delete();
	}

}
