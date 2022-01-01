package Server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ClientDAO.DAO;

/**
 * Servlet implementation class Server
 */
@WebServlet("/Server")
public class Server extends HttpServlet {
	
	String jsonStringRespond = "";
	
	DAO dao = new DAO();
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Server() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		String getdata = request.getParameter("getdata");
		
		

		if (getdata == null) {
			
			PrintWriter out = response.getWriter();
			
			String lockDataJson = request.getParameter("lockdata");
			
			if(lockDataJson != null) {
				
				jsonStringRespond = dao.selectLabRoom(lockDataJson);
				System.out.println(jsonStringRespond);
				out.println(jsonStringRespond);
				out.close();
				
			}
			
			String reader_data_json = request.getParameter("RFIDConstructor");
			
			if(reader_data_json != null) {
				
				System.out.println(reader_data_json);
				
				jsonStringRespond = dao.validateTag(reader_data_json);
				
				out.println(jsonStringRespond);
				
				out.close();
				
			}
			
			String applicationInfo = request.getParameter("ApplicationInfo");
			
			if (applicationInfo != null) {
				String applicationLab = dao.getApplicationRoomStatus(applicationInfo);
				out.println(applicationLab);
				out.close();

			}
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
