package jdromedaplugin.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {

	private File baseDir;
	private File targetDir;
	
	public Project()
	{
		String path = System.getProperty("user.dir");
		baseDir = new File(path);
		targetDir = new File(path + "/webapp");
	}
	
	public File getBasedir() {
		return baseDir;
	}

	public List<String> getCompileSourceRoots() {
		List<String> paths = new ArrayList<>();
		paths.add(baseDir.getAbsolutePath() + "/src/main/java/example");
		return paths;
	}

	public File getTarget() {
		return targetDir;
	}

}
