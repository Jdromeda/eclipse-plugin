package jdromedaplugin.builder;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.jsweet.transpiler.JSweetTranspiler;

public class Executer extends AbstractExecuter {

	
	private JSweetTranspiler transpiler;
	
	public static void main(String args []) throws Exception
	{
		Executer executer = new Executer();
		executer.run();
	}
	
	public void run() throws Exception
	{
		try {
		super.run();
		Project p = getProject();
		
		transpiler = createJSweetTranspiler(p);

		System.out.println("- Starting transpilator process  ... ");
		

		transpile(p, transpiler);
		
		Thread t = new Thread(new ChangeWatcher(getProject(), this));
		t.start();
		
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		startServer();
	}
	
	private void startServer() throws Exception
	{
        // Create a basic Jetty server object that will listen on port 8080.  Note that if you set this to port 0
        // then a randomly available port will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(8080);

        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        ResourceHandler resource_handler = new ResourceHandler();

        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase(getProject().getTarget().getAbsolutePath());

        // Add the ResourceHandler to the server.
        HandlerList handlers = new HandlerList();
    
        
        WebSocketHandler wsHandler = new WebSocketHandler()
        {
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                factory.register(WebSocketReload.class);
            }
        };
        ContextHandler context = new ContextHandler();
        context.setContextPath("/echo");
        context.setHandler(wsHandler);
        handlers.setHandlers(new Handler[] { resource_handler,context, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
        openBrowser();
        server.join();
	}

	private void openBrowser() {
		// TODO Auto-generated method stub
        String url = "http://127.0.0.1:8080/";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}

	@Override
	public void reCompile() {
		transpile(getProject(), transpiler);
		
	}
}
