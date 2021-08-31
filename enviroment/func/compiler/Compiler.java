package enviroment.func.compiler;

import enviroment.Main;
import enviroment.GUI.Console;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Compiler {
	
	private Main main;
	private Console console;
	
	private JavaCompiler compiler;
	private StandardJavaFileManager fileManager;
	private Process program;
	private InputStream err, out;
	
	public Compiler(Main main) {
		this.main=main;
		
		compiler=ToolProvider.getSystemJavaCompiler();
		fileManager=compiler.getStandardFileManager(null, null, null);
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if(console==null) return;
				try {
					if (err!=null) {
						int errAvailable=err.available();
						if (errAvailable>0) {
							byte[] bytes=new byte[errAvailable];
							err.read(bytes);
							console.append(bytes);
						}
					}
					if (out!=null) {
						int outAvailable=out.available();
						if (outAvailable>0) {
							byte[] bytes=new byte[outAvailable];
							out.read(bytes);
							console.append(bytes);
						}
					}
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, 100);
	}
	
	public boolean run(File containerFile, String mainClass) {
		endProcess();
		console.clear();
		if(!containerFile.exists()) {
			return false;
		}
		try {
			Runtime runtime=Runtime.getRuntime();
			program=runtime.exec("java "+mainClass, null, containerFile);
			err=program.getErrorStream();
			out=program.getInputStream();
			
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean compile(File ftc) {
		File[] files=getFiles(ftc).toArray(new File[0]);
		File containerFile=new File(ftc, "SepdronIDE-Program-Build");
		Iterable<? extends JavaFileObject> comUnits=fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
		boolean success=
			compiler.getTask(null,
			fileManager,
			null,
			Arrays.asList(new String[]{"-d", containerFile.getPath()}),
			null,
			comUnits).call();
		return success;
	}
	private List<File> getFiles(File file) {
	  List<File> files=new ArrayList<File>();
	  for (File f : file.listFiles()) {
		if (f.isFile()) {
		  if (f.getName().endsWith(".java")) {
			files.add(f);
		  }
		  continue;
		}
		files.addAll(getFiles(f));
	  }
	  return files;
	}
	
	public void endProcess() {
		if (program!=null && program.isAlive())
			program.destroyForcibly();
	}
	
	public void setConsole(Console console) {
		this.console=console;
	}
}