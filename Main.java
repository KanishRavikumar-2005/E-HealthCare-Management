import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import Modulo.*;

//NOTE. This porject file contains connections to supabase. The API key's were removed for security reasons. 

// https://github.com/kanishravikumar-2005/modulo

public class Main {
    public static void main(String[] args) throws Exception {
        Router router = new Router();

        router.add("/", (req, p) -> {
            if(Context.session().get("role") == null){
                Renderer.redirect("/login");
            }
            else {
                 Renderer.redirect("/dashboard");
            }
            return "";
        }, new String[]{"GET", "POST"});

        router.add("/login", (req, p) -> {
            if(Context.method("POST")){
                String role = Context.input("role");
                if(role.equals("hospital")){
                    Response resp = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/users")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .query("email", "eq."+Context.input("email"))
                        .query("password", "eq."+ Hasher.sha256(Context.input("password")))
                        .uriOut()
                        .get()
                        .send();
                    System.out.println(resp.toString());
                    String userId = resp.get(0).get("id");
                    String p_role = resp.get(0).get("role");
                    String username = resp.get(0).get("username");
                    
                    Context.session().put("username", username);
                    Context.session().put("userid", userId);
                    Context.session().put("role", p_role);

                    Renderer.redirect("/");
                    
                }else if(role.equals("patient")){
                    Response resp = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/patients")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .query("email", "eq."+Context.input("email"))
                        .query("password", "eq."+ Hasher.sha256(Context.input("password")))
                        .uriOut()
                        .get()
                        .send();
                    System.out.println(resp.toString());
                    String userId = resp.get(0).get("id");
                    String p_role = "patient";
                    String username = resp.get(0).get("first_name");
                    
                    Context.session().put("username", username);
                    Context.session().put("userid", userId);
                    Context.session().put("role", p_role);

                    Renderer.redirect("/");
                }
            }
            return Renderer.render("login");
        }, new String[]{"GET", "POST"});

        router.add("/reg/admin", (req, p) -> {
            if(Context.method("POST")){
                String name = Context.input("name");
                String type = Context.input("type");
                String email = Context.input("email");
                String password = Hasher.sha256(Context.input("password"));
                String password_c = Hasher.sha256(Context.input("password_c"));
                String phone = Context.input("number");
                String address = Context.input("address");

                if(password.equals(password_c)){
                    String json_d = String.format("""
                        {
                            "role": "%s",
                            "username": "%s",
                            "email": "%s",
                            "password": "%s",
                            "phone": "%s",
                            "address": "%s",
                            "member": "hospital"
                        }
                    """, type, name, email, password, phone, address);
                    Response insertRes = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/users")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .json(json_d)
                        .post()
                        .send();

                    System.out.println(json_d + " Registration done");
                    System.out.println(insertRes.toString());
                    Renderer.redirect("/login");
                }else{
                     return Renderer.render("staff_reg");
                }
                
            }else{
                 return Renderer.render("staff_reg");
            }
            return "";
        }, new String[]{"GET", "POST"});

        router.add("/reg/patient", (req, p) -> {
            if(Context.method("POST")){
                String fname = Context.input("fname");
                String lname = Context.input("lname");
                String dob = Context.input("dob");
                String gender = Context.input("gender");
                String address = Context.input("address");
                String city = Context.input("city");
                String state = Context.input("state");
                String zipcode = Context.input("zipcode");
                String number = Context.input("number");
                String email = Context.input("email");
                String password = Hasher.sha256(Context.input("password"));
                String confirmPassword = Hasher.sha256(Context.input("confirmPassword"));

                if(password.equals(confirmPassword)){ // Assuming this is the role or user type

                    String json_d = String.format("""
                        {
                            "first_name": "%s",
                            "last_name": "%s",
                            "email": "%s",
                            "password": "%s",
                            "phone": "%s",
                            "address": "%s",
                            "dob": "%s",
                            "gender": "%s",
                            "city": "%s",
                            "state": "%s",
                            "zipcode": "%s",
                            "member": "patient"
                        }
                    """, fname, lname, email, password, number, address, dob, gender, city, state, zipcode);
                    Response insertRes = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/patients")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .json(json_d)
                        .post()
                        .send();

                    System.out.println(json_d + " Registration done");
                    System.out.println(insertRes.toString());
                    Renderer.redirect("/login");
                }else{
                    return Renderer.render("patient_reg");
                }
            }else{
                return Renderer.render("patient_reg");
            }
            return "";
     
        }, new String[]{"GET", "POST"});


        router.add("/dashboard", (req, q) -> {
            String role = Context.session().get("role");
            String name = Context.session().get("username");
            String id = Context.session().get("userid");

            if ("doctor".equals(role)) {
                Response consults = new Request()
                    .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/mapping")
                    .auth("<supabase database API Key - removed due to security reasons>")
                    .query("doctor_id", "eq."+id)
                    .get()
                    .send();

                System.out.println(consults.toString());
                String patDat = Base64.getEncoder().encodeToString(consults.outData().getBytes());

                return Renderer.render("doctor_dash",
                    new String[] {"name", "id", "patients"},
                    new String[] {name, id, patDat}
                );
            } else if ("patient".equals(role)) {

                Response consults = new Request()
                    .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/mapping")
                    .auth("<supabase database API Key - removed due to security reasons>")
                    .query("patient_id", "eq."+id)
                    .get()
                    .send();
                
                Response doctors = new Request()
                    .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/users")
                    .auth("<supabase database API Key - removed due to security reasons>")
                    .query("role", "eq.doctor")
                    .get()
                    .send();

                List<JsonObjectWrapper> availableDoctors = new ArrayList<>();
                String patientId = Context.session().get("userid"); // assuming patient is logged in

                for (int i = 0; i < doctors.size(); i++) {
                    String doctorId = doctors.get(i).get("id");
                    Response mappings = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/mapping")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .query("doctor_id", "eq." + doctorId)
                        .query("status", "eq.true")
                        .get()
                        .send();

                    int activeCount = mappings.size();
                    boolean patientMapped = false;
                    for (int j = 0; j < mappings.size(); j++) {
                        String pid = mappings.get(j).get("patient_id");
                        if (pid != null && pid.equals(patientId)) {
                            patientMapped = true;
                            break;
                        }
                    }

                    // 3. Filter logic
                    if (patientMapped) {
                        continue; // firewall check: already mapped to patient
                    }
                    if (activeCount >= 3) {
                        continue; // too many mappings
                    }
                    availableDoctors.add(doctors.get(i)); 

                }

                StringBuilder sb = new StringBuilder();
                sb.append("[");

                for (int i = 0; i < availableDoctors.size(); i++) {
                    JsonObjectWrapper doc = availableDoctors.get(i);
                    sb.append("{");
                    sb.append("\"id\":").append("\"").append(doc.get("id")).append("\",");
                    sb.append("\"username\":").append("\"").append(doc.get("username")).append("\"");
                    sb.append("}");
                    if (i < availableDoctors.size() - 1) sb.append(",");
                }

                sb.append("]");
                String jsonOutput = sb.toString();
                System.out.println(jsonOutput);
                String docData = Base64.getEncoder().encodeToString(jsonOutput.toString().getBytes());
                String patDat = Base64.getEncoder().encodeToString(consults.outData().getBytes());
                System.out.println(consults.toString());

                if(Context.method("POST")){
                    String docid = Context.input("doctor_id");
                    String docnam = Context.input("doctor_name");
                    String session_ref = Hasher.sha512(Context.charId(19));
                    String sessions_ref = String.format("""
                        {
                            "patient_id": "%s",
                            "doctor_id": "%s",
                            "status": true,
                            "session_reference_id": "%s",
                            "doc_name": "%s",
                            "patient_name": "%s"
                        }
                    """, Context.session().get("userid"), docid, session_ref, docnam, Context.session().get("username"));

                    Response mappers = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/mapping")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .json(sessions_ref)
                        .post()
                        .send();
                    System.out.println(mappers.toString());
                    Renderer.redirect("/dashboard");
                }

                return Renderer.render("patient_dash",
                    new String[] {"name", "id", "docs", "pats"},
                    new String[] {name, id, docData, patDat}
                );

            } else {
                Renderer.redirect("/");
                return ""; // still needs a return even if redirecting
            }
        }, new String[]{"GET", "POST"});


        router.add("/consultation/:cid", (req, q)-> {

            String sid = q.get("cid");
            

            Response const_detail = new Request()
                .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/mapping")
                .auth("<supabase database API Key - removed due to security reasons>")
                .query("session_reference_id", "eq."+sid)
                .get()
                .send();

            System.out.println(const_detail.toString());
            String person_id = "";
            String who = "";
            String who_name = "";
            String me = "";
            String me_name = "";
            if(Context.session().get("role").equals("doctor")){
                person_id = const_detail.get(0).get("doctor_id");
                who = "patient";
                who_name = const_detail.get(0).get("patient_name");
                me = "doctor";
                me_name = "Dr. "+Context.session().get("username");
            }else if(Context.session().get("role").equals("patient")){
                person_id = const_detail.get(0).get("patient_id");
                who = "doctor";
                who_name = const_detail.get(0).get("doc_name");
                me = "patient";
                me_name = Context.session().get("username");
            }else{
                Renderer.redirect("/");
            }
            System.out.println(person_id);
            if(!person_id.equals(Context.session().get("userid"))){
                Renderer.redirect("/");
            }

            String cdets = Base64.getEncoder().encodeToString(const_detail.outData().getBytes());
            Response chats = new Request()
                .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/chats")
                .auth("<supabase database API Key - removed due to security reasons>")
                .query("session_id", "eq."+sid)
                .get()
                .send();

            String messages = Base64.getEncoder().encodeToString(chats.outData().getBytes());

            if(Context.method("POST")){
                System.out.println("Offing");
                if(Context.input("end").equals("final")){
                    String updated = String.format("""
                        {
                            "status": false
                        }
                    """);

                    Response ending = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/mapping")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .query("session_reference_id", "eq." + sid)
                        .patch()
                        .json(""" 
                            { "status": false,
                             "auxid": "uid"
                            } 
                        """)
                        .send();
                    System.out.println(ending.toString());
                    
                }else{
                    String message = Context.input("message");
                    String message_data = String.format("""
                        {
                            "message_id": "%s",
                            "message_from": "%s",
                            "from_name": "%s",
                            "message": "%s",
                            "session_id": "%s"
                        }
                    """, Hasher.sha256(message), me, me_name, message, sid);
                    if(!message.isEmpty()){
                    Response outers = new Request()
                        .uri("https://<supabase database url - removed due to security reasons>>.supabase.co/rest/v1/chats")
                        .auth("<supabase database API Key - removed due to security reasons>")
                        .json(message_data)
                        .post()
                        .send();
                    System.out.println(outers.toString());
                    Renderer.redirect("/consultation/"+sid);
                }
                }

                
                

            }

            return Renderer.render("consultation", 
                new String[]{"session_id", "dets", "who", "who_name", "chats"},
                new String[]{sid, cdets, who, who_name, messages}
            );

        }, new String[]{"GET", "POST"});

        router.listen("127.0.0.1", 8080, true);
    }
}
