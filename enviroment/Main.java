package enviroment;

import enviroment.GUI.GUI;
import enviroment.func.compiler.Compiler;

public class Main {
	
	public GUI gui;
	public Compiler compiler;
	
	private Main() {
		gui=new GUI(this);
		compiler=new Compiler(this);
		gui.initialize();
	}
	
	public GUI getGUI() {
		return gui;
	}
	public Compiler getCompiler() {
		return compiler;
	}
	
	public static void main(String[] args) {
		new Main();
	}
}