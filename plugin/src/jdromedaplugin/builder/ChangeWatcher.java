package jdromedaplugin.builder;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Timer;
import java.util.TimerTask;

public class ChangeWatcher implements Runnable {

	private Project p;
	private AbstractExecuter executer;
	private Timer timer;

	public ChangeWatcher(Project p, AbstractExecuter executer) {
		this.executer = executer;
		this.p = p;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(p.getCompileSourceRoots().get(0));
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_MODIFY);
					return FileVisitResult.CONTINUE;
				}
			});
			WatchKey key;
			while ((key = watchService.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					// process
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							timer = null;
							try {
								executer.reCompile();
								WebSocketReload.reloadAll();
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}, 2000);

				}
				key.reset();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
