package jdromedaplugin.builder;


import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.EcmaScriptComplianceLevel;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.ModuleResolution;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ProcessUtil;

public abstract class AbstractExecuter {

	protected EcmaScriptComplianceLevel targetVersion = EcmaScriptComplianceLevel.ES6;

	protected ModuleKind module;

	protected String outDir = "webapp/js";

	protected String tsOut = "webapp/ts";

	protected Boolean tsserver;
	
	protected Boolean bundle = true;

	protected Boolean declaration;

	protected Boolean tsOnly = false;

	protected String dtsOut;

	protected Boolean sourceMap = true;
	
	protected String sourceRoot;

	protected Boolean verbose = false;

	protected Boolean veryVerbose = false;

	protected Boolean ignoreDefinitions;

	protected File candiesJsOut;

	protected String[] includes;

	protected String[] excludes;

	protected String encoding;

	protected Boolean noRootDirectories;

	protected Boolean enableAssertions;

	protected Boolean disableSinglePrecisionFloats;

	protected File jdkHome = new File("${java.home}");

	protected String extraSystemPath;

	protected ModuleResolution moduleResolution;
	
	protected String factoryClassName;
	
	protected Boolean ignoreTypeScriptErrors;

	protected File header;

	protected File workingDir;

	private Project p;
	
	public Project createProject()
	{
		p = new Project();
		
		return p;
	}
	
	public Project getProject()
	{
		return p;
	}
	
	
	public void run() throws Exception
	{
		createProject();
	};

	private void logInfo(String text)
	{
		System.out.println(text);
	}
	
	protected JSweetTranspiler createJSweetTranspiler(Project project) {

		try {

			List<File> dependenciesFiles = getCandiesJars();

			String classPath = dependenciesFiles.stream() //
					.map(f -> f.getAbsolutePath()) //
					.collect(joining(System.getProperty("path.separator")));

			logInfo("classpath: " + classPath);

			File tsOutputDir = getTsOutDir();

			File jsOutDir = getJsOutDir();

			File declarationOutDir = getDeclarationsOutDir();

			logInfo("jsOut: " + jsOutDir);
			logInfo("bundle: " + bundle);
			logInfo("tsOut: " + tsOutputDir);
			logInfo("tsOnly: " + tsOnly);
			logInfo("tsserver: " + tsserver);
			logInfo("declarations: " + declaration);
			logInfo("ignoreDefinitions: " + ignoreDefinitions);
			logInfo("declarationOutDir: " + declarationOutDir);
			logInfo("candiesJsOutDir: " + candiesJsOut);
			logInfo("ecmaTargetVersion: " + targetVersion);
			logInfo("moduleKind: " + module);
			logInfo("sourceMap: " + sourceMap);
			logInfo("sourceRoot: " + sourceRoot);
			logInfo("verbose: " + verbose);
			logInfo("veryVerbose: " + veryVerbose);
			logInfo("jdkHome: " + jdkHome);
			logInfo("factoryClassName: " + factoryClassName);

			JSweetConfig.initClassPath(jdkHome.getAbsolutePath());

			logInfo("extraSystemPath: " + extraSystemPath);
			if (isNotBlank(extraSystemPath)) {
				ProcessUtil.addExtraPath(extraSystemPath);
			}

			LogManager.getLogger("org.jsweet").setLevel(Level.WARN);

			if (verbose != null && verbose) {
				LogManager.getLogger("org.jsweet").setLevel(Level.DEBUG);
			}
			if (veryVerbose != null && veryVerbose) {
				LogManager.getLogger("org.jsweet").setLevel(Level.ALL);
			}

			JSweetFactory factory = new JSweetFactory();

			if (workingDir != null && !workingDir.isAbsolute()) {
				workingDir = new File(getBaseDirectory(), workingDir.getPath());
			}

			JSweetTranspiler transpiler = new JSweetTranspiler(getBaseDirectory(), null, factory, workingDir,
					tsOutputDir, jsOutDir, candiesJsOut, classPath);
			transpiler.setTscWatchMode(false);
			if (targetVersion != null) {
				transpiler.setEcmaTargetVersion(targetVersion);
			}
			if (module != null) {
				transpiler.setModuleKind(module);
			}
			if (bundle != null) {
				transpiler.setBundle(bundle);
			}
			if (sourceMap != null) {
				transpiler.setGenerateSourceMaps(sourceMap);
			}
			File sourceRoot = getSourceRoot();
			if (sourceRoot != null) {
				transpiler.setSourceRoot(sourceRoot);
			}
			if (encoding != null) {
				transpiler.setEncoding(encoding);
			}
			if (noRootDirectories != null) {
				transpiler.setNoRootDirectories(noRootDirectories);
			}
			if (enableAssertions != null) {
				transpiler.setIgnoreAssertions(!enableAssertions);
			}
			if (declaration != null) {
				transpiler.setGenerateDeclarations(declaration);
			}
			if (declarationOutDir != null) {
				transpiler.setDeclarationsOutputDir(declarationOutDir);
			}
			if (ignoreDefinitions != null) {
				transpiler.setGenerateDefinitions(!ignoreDefinitions);
			}
			if (tsOnly != null) {
				transpiler.setGenerateJsFiles(!tsOnly);
			}
			if (ignoreTypeScriptErrors != null) {
				transpiler.setIgnoreTypeScriptErrors(ignoreTypeScriptErrors);
			}
			if (header != null) {
				transpiler.setHeaderFile(header);
			}
			if (disableSinglePrecisionFloats != null) {
				transpiler.setDisableSinglePrecisionFloats(disableSinglePrecisionFloats);
			}
			if (moduleResolution != null) {
				transpiler.setModuleResolution(moduleResolution);
			}
			if (tsOutputDir != null) {
				transpiler.setTsOutputDir(tsOutputDir);
			}
			if (jsOutDir != null) {
				transpiler.setJsOutputDir(jsOutDir);
			}
			transpiler.setTscWatchMode(true);

			return transpiler;

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	protected File getDeclarationsOutDir() throws IOException {
		File declarationOutDir = null;
		if (isNotBlank(this.dtsOut)) {
			File dtsOutFile = new File(this.dtsOut);
			if (!dtsOutFile.isAbsolute()) {
				dtsOutFile = new File(getBaseDirectory(), this.dtsOut);
			}
			return dtsOutFile.getCanonicalFile();
		}
		return declarationOutDir;
	}

	protected File getSourceRoot() throws IOException {
		File sourceRoot = null;
		if (isNotBlank(this.sourceRoot)) {
			File sourceRootFile = new File(this.sourceRoot);
			if (!sourceRootFile.isAbsolute()) {
				sourceRootFile = new File(getBaseDirectory(), this.sourceRoot);
			}
			return sourceRootFile.getCanonicalFile();
		}
		return sourceRoot;
	}

	protected File getJsOutDir() throws IOException {
		if (isNotBlank(this.outDir)) {
			File jsOutFile = new File(this.outDir);
			if (!jsOutFile.isAbsolute()) {
				jsOutFile = new File(getBaseDirectory(), this.outDir);
			}
			return jsOutFile.getCanonicalFile();
		} else {
			return null;
		}
	}

	protected File getBaseDirectory() throws IOException {
		return getProject().getBasedir().getAbsoluteFile();
	}

	protected File getTsOutDir() throws IOException {
		if (isNotBlank(this.tsOut)) {
			File tsOutFile = new File(this.tsOut);
			if (!tsOutFile.isAbsolute()) {
				tsOutFile = new File(getBaseDirectory(), this.tsOut);
			}
			return tsOutFile.getCanonicalFile();

		} else {
			return null;
		}
	}

	protected List<File> getCandiesJars() {
		List<File> dependenciesFiles = new LinkedList<>();
		dependenciesFiles.add(new File(System.getProperty("user.dir") + "/..//core-lib/es6/target/jdromeda-core-1.0-SNAPSHOT.jar"));

		logInfo("candies jars: " + dependenciesFiles);

		return dependenciesFiles;	
	}
	
	protected void transpile(Project project, JSweetTranspiler transpiler) throws IllegalArgumentException {
		try {
			TranspilationHandler transpilationHandler = new TranspilationHandler();
			try {

				SourceFile[] sources = collectSourceFiles(project);

				transpiler.transpile(transpilationHandler, sources);

			} catch (NoClassDefFoundError error) {
				error.printStackTrace();
				transpilationHandler.report(JSweetProblem.JAVA_COMPILER_NOT_FOUND, null,
						JSweetProblem.JAVA_COMPILER_NOT_FOUND.getMessage());
			}

			int errorCount = transpilationHandler.getErrorCount();

			if (errorCount > 0) {
				throw new IllegalArgumentException("transpilation failed with " + errorCount + " error(s) and "
						+ transpilationHandler.getWarningCount() + " warning(s)");
			} else {

				if (transpilationHandler.getWarningCount() > 0) {
					logInfo("transpilation completed with " + transpilationHandler.getWarningCount() + " warning(s)");
				} else {
					logInfo("transpilation successfully completed with no errors and no warnings");
				}

			}

		} catch (Exception e) {
			logInfo("transpilation failed");
			e.printStackTrace();
			throw new IllegalArgumentException("transpilation failed", e);
		}
	}
	
	protected SourceFile[] collectSourceFiles(Project project) {

		logInfo("source includes: " + ArrayUtils.toString(includes));
		logInfo("source excludes: " + ArrayUtils.toString(excludes));

		List<String> sourcePaths = project.getCompileSourceRoots();
		logInfo("sources paths: " + sourcePaths);

		List<SourceFile> sources = new LinkedList<>();
		for (String sourcePath : sourcePaths) {
			scanForJavaFiles(sources, new File(sourcePath));
		}


		logInfo("sourceFiles=" + sources);

		return sources.toArray(new SourceFile[0]);
	}

	private void scanForJavaFiles(List<SourceFile> sources, File sourceDirectory) {
		if (!sourceDirectory.exists()) {
			logInfo(sourceDirectory.getAbsolutePath() + " not exist");
			return;
		}

		DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setBasedir(sourceDirectory);
		dirScanner.setIncludes(includes);
		dirScanner.setExcludes(excludes);
		dirScanner.scan();

		for (String includedPath : dirScanner.getIncludedFiles()) {
			if (includedPath.endsWith(".java")) {
				sources.add(new SourceFile(new File(sourceDirectory, includedPath)));
			}
		}
	}

	public abstract void reCompile();
}
