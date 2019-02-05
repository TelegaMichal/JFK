import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;

import javax.tools.*;
import java.io.*;
import java.util.Arrays;

import static com.github.javaparser.JavaParser.parse;

public class Main
{
	public static void main(String[] args)
	{
		final String path = "src/Class.java";
		final String finalPath = "src/ClassNew.java";
		
		CompilationUnit compilationUnit;
		
		FileInputStream fileInputStream = null;
		try
		{
			fileInputStream = new FileInputStream(path);
		} catch (FileNotFoundException e)
		{
			System.out.println("Cannot find a file.");
		}
		
		compilationUnit = parse(fileInputStream);
		
		compilationUnit.getChildNodesByType(BinaryExpr.class).forEach(Main::sprawdz);
		compilationUnit.getClassByName("Class").get().setName("ClassNew");
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(compilationUnit.toString());
		
		FileWriter fileWriter = null;
		try
		{
			fileWriter = new FileWriter(finalPath, false);
			fileWriter.write(stringBuilder.toString());
		} catch (IOException e)
		{
			System.out.println("Cannot write to file.");
		}finally
		{
			try
			{
				fileWriter.close();
			} catch (IOException e)
			{
				System.out.println("Cannot close a file.");
			}
		}
		
		File[] files = {new File(finalPath)};
		String[] options = { "-d", "out//production//TelegaJFKLab2" };
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
			compiler.getTask(null, fileManager, diagnostics, Arrays.asList(options), null, compilationUnits).call();
			diagnostics.getDiagnostics().forEach(d -> System.out.println(d.getMessage(null)));
		} catch (IOException e)
		{
			System.out.println("Error with compiling");
		}
	}
	
	private static void sprawdz(BinaryExpr binaryExpr)
	{
		String operator = binaryExpr.getOperator().toString();
		
		if(operator.equals("EQUALS") || operator.equals("NOT_EQUALS"))
		{
			Expression right = binaryExpr.getRight();
			
			if(right.getClass().toString().equals("class com.github.javaparser.ast.expr.BooleanLiteralExpr")
				|| right.getClass().toString().equals("class com.github.javaparser.ast.expr.LiteralStringValueExpr")
			)
			{
				Expression left = binaryExpr.getLeft();
				binaryExpr.setLeft(right);
				binaryExpr.setRight(left);
			}
			else if(right.getClass().getSuperclass().toString().equals("class com.github.javaparser.ast.expr" +
					".LiteralStringValueExpr"))
			{
				Expression left = binaryExpr.getLeft();
				binaryExpr.setLeft(right);
				binaryExpr.setRight(left);
			}
		}
	}
}