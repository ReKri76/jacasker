import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
class tasker{
    private Stack <Integer> stk = new Stack();
    private  HashMap <Integer, String[]> tasks = new HashMap<>();
    tasker(tasker copy){
        this.stk=copy.stk;
        this.tasks=copy.tasks;
    }
    tasker() throws IOException{
        List <Integer> stc = new ArrayList<Integer>();
       File file = new File("Tasks.json");
       if (!file.exists()) file.createNewFile();
       else{
        try (FileReader reader = new FileReader("Tasks.json")) {
            BufferedReader reder = new BufferedReader(reader);
            String line = reder.readLine();
           while (line != null){
            String[] str = line.split(" ");    
                int id = Integer.parseInt(str[0]);
                String[] info = {str[1], str[2]};
                tasks.put(id,info);
                stc.add(id);
                line = reder.readLine();
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }}
        if (stc.size()==0);
        else{
        Collections.sort(stc);
        int a=stc.get(0);
        for (int x=0; x<stc.size(); x++){
            int b=stc.get(x);
            if (b-a>1) {
                for (int u=a; u<b; u++){
                    stk.push(u);
                }
            }
            a=b;
        }}
    }
    public void add(String name){
        String[] info ={name,"in"};
        if (stk.empty()){
            int id =tasks.size();
            tasks.put(id,info);
            System.out.println("Задача "+info[0]+": "+info[1]+" - "+id+" создана.");}
        else{
            int id = stk.pop();
            tasks.put(id,info);
            System.out.println("Задача "+info[0]+": "+info[1]+" - "+id+" создана.");}
   }
   public void add(String name, String progress){
        if (progress.equals("in")||progress.equals("skip")||progress.equals("done")){
            String[] info ={name, progress};
            if (stk.empty()){
                int id = tasks.size();
                tasks.put(id,info);
                System.out.println("Задача "+info[0]+": "+info[1]+" - "+id+" создана.");}
            else{
                int id = stk.pop();
                tasks.put(stk.pop(),info);
                System.out.println("Задача "+info[0]+": "+info[1]+" - "+id+" создана.");}}
        else System.out.println("Допустимы только значения 'in'- в процессе, 'skip'- отложено и 'done' - выполнено");
    }
    public void update(int id, String progress) {
        if (progress.equals("in")||progress.equals("skip")||progress.equals("done")){
        if (tasks.containsKey(id)) {
            String[] info = tasks.get(id);
            info[1] = progress;
            tasks.put(id, info);
            System.out.println("Задача обновлена");
        } else 
            System.out.println("Неверный id задачи");}
        else System.out.println("Допустимы только значения 'in'- в процессе, 'skip'- отложено и 'done' - выполнено");
    }
    public void view(int id){
        if (tasks.containsKey(id)){
            String[] info = tasks.get(id);
            System.out.println(info[0]+": "+info[1]+" - "+id+".");}
        else 
            System.out.println("неверный id задачи"); 
    }
    public void delete(int id){
        if (tasks.containsKey(id)){
            tasks.remove(id);
            stk.push(id);
            System.out.println("Здача удалена");}
        else 
            System.out.println("неверный id задачи");
    }
    public void viewAll(){
        for (Map.Entry<Integer, String[]> x: tasks.entrySet()){
            String[] info = x.getValue();
            System.out.println("Task "+info[0]+": "+info[1]+", id - "+x.getKey()+"." );
            }
        }
     public void viewAllDone(String f){
        if (f.equals("not")){
        for (Map.Entry<Integer, String[]> x: tasks.entrySet()){
            String[] info = x.getValue();
            if (!info[1].equals("done"))
                System.out.println("Task "+info[0]+": "+info[1]+", id - "+x.getKey()+"." );}
                }
        else{
            for (Map.Entry<Integer, String[]> x: tasks.entrySet()){
                String[] info = x.getValue();
                if (info[1].equals("done"))
                    System.out.println("Task "+info[0]+": "+info[1]+", id - "+x.getKey()+"." );}
        }
        }
    public void viewAllProgress(){
        for (Map.Entry<Integer, String[]> x: tasks.entrySet()){
            String[] info = x.getValue();
            if (info[1].equals("in"))
                System.out.println("Task "+info[0]+": "+info[1]+ ", id -  "+x.getKey()+"." );}
                }
            

    public void save() {
        try (FileWriter saver = new FileWriter("Tasks.json", false);
            BufferedWriter writer = new BufferedWriter(saver)) {
            for (Map.Entry<Integer, String[]> x : tasks.entrySet()) {
                String str = String.valueOf(x.getKey())+" "+x.getValue()[0]+" "+x.getValue()[1];
                writer.write(str);
                writer.newLine();
            }
            System.out.println("Сохранено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  
} 
class voyager implements Runnable{
    tasker in;
    String[] com;
    voyager(tasker out, String[] inst){
        in = out;
        com = inst;
    }
    public void run (){
        synchronized(in){ int size=com.length;
        String main = com[0];
        if (size==1){
            if (main.equals("save")) in.save();
            else if (main.equals("viewAll")) in.viewAll();
            else if (main.equals("viewAllProgress")) in.viewAllProgress();
            else if (main.equals("viewAllDone")) in.viewAllDone("");
            else if (main.equals("viewAllNotDone")) in.viewAllDone("not");
            else if (main.equals("new")) in.add("defualt_name");
            else if (main.equals("help")) {
            System.out.println("save -сохранение");
            System.out.println("viewAll - показать все задачи");
            System.out.println("viewAllProgress - показать все задачи в процессе выполнения");
            System.out.println("viewAllDone - показать все завершенные задачи");
            System.out.println("viewAllNotDone - показать все незаврешенные задачи");
            System.out.println("new - создать новую задачу, если последующими двумя аргументами не задать парамерты задачи(имя и прогресс выполнения), то они будут заданы по умлочанию");
            System.out.println("view {id} - просмотор задачи по id ");
            System.out.println("delete {id} - удаление задачи по id");
            System.out.println("update {id progress} - обновление статуса задачи по id");
            System.out.println("exit - завершить программу");
            System.out.println("P.S фигурные скобки в аргументах указывать не нужно");}
            else System.out.println("Неверный набор аргументов");
        }
        else if (size==2){
            if (main.equals("view")) in.view(Integer.parseInt(com[1]));
            else if (main.equals("delete")) in.delete(Integer.parseInt(com[1]));
            else if (main.equals("new")) in.add(com[1]);
            else System.out.println("Неверный набор аргументов");
        }
        else if (size==3){
            if (main.equals("new")) in.add(com[1], com[2]);
            else if (main.equals("update")) in.update(Integer.parseInt(com[1]), com[2]);
            else System.out.println("Неверный набор аргументов");
        }
        else System.out.println("Неверный набор аргументов");
    }}
}
public class javasker {
    public static void main(String[] args) throws IOException{
        System.out.println("help - для помощи");
        tasker tasker= new tasker();
        Scanner reader = new Scanner(System.in);
        while(true){
            String input = reader.nextLine();
            if (input.equals("exit")){tasker.save(); break;}
            String[] info = input.split(" ");
            voyager voider = new voyager(tasker, info);
            Thread yakut = new Thread(voider);
            yakut.start();
        }
        reader.close();
    }
}
