package jdromedaplugin.builder;

import java.util.List;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;

public class TranspilationHandler extends ErrorCountTranspilationHandler {


	protected List<JSweetProblem> ignoredProblems;
	
	public TranspilationHandler() {
		super(new ConsoleTranspilationHandler());
	}
	
	@Override
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
		if (ignoredProblems != null && ignoredProblems.contains(problem)) {
			return;
		}
		super.report(problem, sourcePosition, message);
	}
}
