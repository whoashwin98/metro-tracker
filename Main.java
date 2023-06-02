import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public class WT {
        int weight;
        int time;

        public WT(int weight, int time) {
            this.weight = weight;
            this.time = time;
        }
    }

    public class Vertex {
        HashMap<String, WT> nbrs = new HashMap<>();
    }

    static HashMap<String, Vertex> vertices;
    static HashMap<String, ArrayList<String>> lines;

    public Main() {
        vertices = new HashMap<>();
        lines = new HashMap<>();
    }

    public void addStationToLine(String line, String station) {
        ArrayList<String> stations;
        if (!lines.containsKey(line)) {
            stations = new ArrayList<>();
        } else {
            stations = lines.get(line);
        }
        stations.add(station);
        lines.put(line, stations);
    }

    public int numVertex() {
        return vertices.size();
    }

    public boolean containsVertex(String vname) {
        return vertices.containsKey(vname);
    }

    public void addVertex(String vname) {
        Vertex vtx = new Vertex();
        vertices.put(vname, vtx);
    }

    public int numEdges() {
        ArrayList<String> keys = new ArrayList<>(vertices.keySet());
        int count = 0;

        for (String key : keys) {
            Vertex vtx = vertices.get(key);
            count = count + vtx.nbrs.size();
        }

        return count / 2;
    }

    public boolean containsEdge(String vname1, String vname2) {
        Vertex vtx1 = vertices.get(vname1);
        Vertex vtx2 = vertices.get(vname2);

        if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
            return false;
        }

        return true;
    }

    public void addEdge(String vname1, String vname2, int weight, int time) {
        Vertex vtx1 = vertices.get(vname1);
        Vertex vtx2 = vertices.get(vname2);

        if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
            return;
        }

        vtx1.nbrs.put(vname2, new WT(weight, time));
        vtx2.nbrs.put(vname1, new WT(weight, time));
    }

    public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) {
        // CHECK FOR DIRECT EDGE
        if (containsEdge(vname1, vname2)) {
            return true;
        }

        // MARK AS DONE
        processed.put(vname1, true);

        Vertex vtx = vertices.get(vname1);
        ArrayList<String> nbrs = new ArrayList<>(vtx.nbrs.keySet());

        // TRAVERSE THE NEIGHBOURS OF THE VERTEX
        for (String nbr : nbrs) {

            if (!processed.containsKey(nbr))
                if (hasPath(nbr, vname2, processed))
                    return true;
        }

        return false;
    }

    public void displayStations() {
        System.out.println("\n***********************************************************************\n");
        ArrayList<String> keys = new ArrayList<>(vertices.keySet());
        int i = 1;
        for (String key : keys) {
            System.out.println(i + ". " + key);
            i++;
        }
        System.out.println("\n***********************************************************************\n");
    }

    public void displayMap() {
        System.out.println("\n***********************************************************************\n");
        ArrayList<String> keys = new ArrayList<>(lines.keySet());
        int i = 1;
        for (String key : keys) {
            System.out.println(i + ". " + key);
            i++;
            int j = 1;
            for (String station : lines.get(key)) {
                System.out.println("\t" + j + ". " + station);
                j++;
            }
            System.out.println();
        }
        System.out.println("\n***********************************************************************\n");
    }

    // A CLASS USED FOR IMPLEMENTING THE DIJKSTRA'S ALGORITHM
    // DATA MEMBERS: VERTEX NAME, PATH SO FAR (STRING) AND COST
    private class DijkstraPair implements Comparable<DijkstraPair> {
        String vname;
        String psf;
        int cost;

        // COMPARES COST OF TWO VALUES
        // If the result is negative, it indicates that the current object has a lower
        // cost and should be considered "less" than the compared object. If the result
        // is positive, it indicates that the current object has a higher cost and
        // should be considered "greater" than the compared object. If the result is
        // zero, it indicates that both objects have the same cost.
        @Override
        public int compareTo(DijkstraPair o) {
            return o.cost - this.cost;
        }
    }

    public int dijkstra(String src, String des, boolean nan) {
        // stores the shortest path cost
        int val = 0;

        // store a map for each vertex, where value is the dijkstra pair associated with
        // that vertex
        HashMap<String, DijkstraPair> map = new HashMap<>();

        // heap used to store the dijkstra pairs based on cost
        Heap<DijkstraPair> heap = new Heap<>();

        // iterate through all the vertices
        for (String key : vertices.keySet()) {
            // for each vertex, initialise a dijkstra pair
            DijkstraPair np = new DijkstraPair();
            np.vname = key;
            np.cost = Integer.MAX_VALUE;

            // for source node, the cost will be 0, and we add the source node to the path
            // so far (psf)
            if (key.equals(src)) {
                np.cost = 0;
                np.psf = key + " -> ";
            }

            // add to the heap and map
            heap.add(np);
            map.put(key, np);
        }

        // keep removing the pairs while heap is not empty
        while (!heap.isEmpty()) {
            // get the current heap top element
            DijkstraPair rp = heap.remove();

            // if the destination is reached, set the cost and break out of the loop
            if (rp.vname.equals(des)) {
                val = rp.cost;
                break;
            }

            // remove the vertex from the map since it has been processed
            map.remove(rp.vname);

            // get the vertex object and using that get the neighbours
            Vertex v = vertices.get(rp.vname);
            for (String nbr : v.nbrs.keySet()) {
                // if the neighbour vertex has not yet been processed
                if (map.containsKey(nbr)) {
                    // get the old cost
                    int oc = map.get(nbr).cost;
                    Vertex k = vertices.get(rp.vname);

                    // calculate the new cost for reaching the vertex
                    int nc;
                    if (nan)
                        nc = rp.cost + k.nbrs.get(nbr).time;
                    else
                        nc = rp.cost + k.nbrs.get(nbr).weight;

                    // update the cost if new cost is lesser
                    if (nc < oc) {
                        DijkstraPair gp = map.get(nbr);
                        gp.psf = rp.psf + nbr + " -> ";
                        gp.cost = nc;

                        // since the cost has changed, update the priority of the same in the heap
                        heap.updatePriority(gp);
                    }
                }
            }
        }

        // Print the path
        System.out.println("Path: ");
        System.out.println(map.get(des).psf + " END");
        System.out.println(map.get(des).psf + " END");

        // finally, return the value which will be the
        // minimum cost to reach destination from source
        return val;
    }

    public static void CreateMap(Main g) {
        g.addVertex("Noida Electronic City");
        g.addVertex("Mayur Vihar Phase 1");
        g.addVertex("Yamuna Bank");
        g.addVertex("Anand Vihar");
        g.addVertex("Vaishali");
        g.addVertex("Mandi House");
        g.addVertex("Rajiv Chowk");
        g.addVertex("Kirti Nagar");
        g.addVertex("Rajouri Garden");
        g.addVertex("Dwarka");
        g.addVertex("Dwarka Sector 21");
        g.addVertex("Samaypur Badli");
        g.addVertex("Azadpur");
        g.addVertex("Kashmere Gate");
        g.addVertex("Rajiv Chowk");
        g.addVertex("Central Secretariat");
        g.addVertex("Dilli Haat INA");
        g.addVertex("Hauz Khas");
        g.addVertex("HUDA City Centre");
        g.addVertex("New Bus Adda");
        g.addVertex("Welcome");
        g.addVertex("Inderlok");
        g.addVertex("Punjabi Bagh West");
        g.addVertex("Netaji Subhash Place");
        g.addVertex("Rithala");
        g.addVertex("Bahadurgarh City Park");
        g.addVertex("Ashok Park Main");
        g.addVertex("Ballabhgarh");
        g.addVertex("Kalkaji Mandir");
        g.addVertex("Lajpat Nagar");
        g.addVertex("New Delhi");
        g.addVertex("IGI Airport T3");
        g.addVertex("Shiv Vihar");
        g.addVertex("Hazrat Nizamuddin");
        g.addVertex("Majlis Park");
        g.addVertex("Janakpuri West");
        g.addVertex("Botanical Garden");
        g.addVertex("Dhansa Bus Stand");

        // Blue Line
        g.addEdge("Noida Electronic City", "Botanical Garden", 12, 18);
        g.addEdge("Botanical Garden", "Mayur Vihar Phase 1", 8, 13);
        g.addEdge("Mayur Vihar Phase 1", "Yamuna Bank", 5, 6);
        g.addEdge("Yamuna Bank", "Anand Vihar", 9, 12);
        g.addEdge("Anand Vihar", "Vaishali", 3, 4);
        g.addEdge("Yamuna Bank", "Mandi House", 8, 7);
        g.addEdge("Mandi House", "Rajiv Chowk", 2, 4);
        g.addEdge("Rajiv Chowk", "Kirti Nagar", 8, 15);
        g.addEdge("Kirti Nagar", "Rajouri Garden", 4, 6);
        g.addEdge("Rajouri Garden", "Janakpuri West", 5, 10);
        g.addEdge("Janakpuri West", "Dwarka", 6, 11);
        g.addEdge("Dwarka", "Dwarka Sector 21", 10, 18);
        g.addStationToLine("Blue", "Noida Electronic City");
        g.addStationToLine("Blue", "Botanical Garden");
        g.addStationToLine("Blue", "Mayur Vihar Phase 1");
        g.addStationToLine("Blue", "Yamuna Bank");
        g.addStationToLine("Blue", "Anand Vihar");
        g.addStationToLine("Blue", "Vaishali");
        g.addStationToLine("Blue", "Mandi House");
        g.addStationToLine("Blue", "Rajiv Chowk");
        g.addStationToLine("Blue", "Kirti Nagar");
        g.addStationToLine("Blue", "Rajouri Garden");
        g.addStationToLine("Blue", "Janakpuri West");
        g.addStationToLine("Blue", "Dwarka");
        g.addStationToLine("Blue", "Dwarka Sector 21");

        // Yellow Line
        g.addEdge("Samaypur Badli", "Azadpur", 8, 11);
        g.addEdge("Azadpur", "Kashmere Gate", 7, 13);
        g.addEdge("Kashmere Gate", "New Delhi", 4, 6);
        g.addEdge("New Delhi", "Rajiv Chowk", 1, 2);
        g.addEdge("Rajiv Chowk", "Central Secretariat", 2, 4);
        g.addEdge("Central Secretariat", "Dilli Haat INA", 6, 9);
        g.addEdge("Dilli Haat INA", "Hauz Khas", 5, 7);
        g.addEdge("Hauz Khas", "HUDA City Centre", 19, 31);
        g.addStationToLine("Yellow", "Samaypur Badli");
        g.addStationToLine("Yellow", "Azadpur");
        g.addStationToLine("Yellow", "Kashmere Gate");
        g.addStationToLine("Yellow", "New Delhi");
        g.addStationToLine("Yellow", "Rajiv Chowk");
        g.addStationToLine("Yellow", "Central Secretariat");
        g.addStationToLine("Yellow", "Dilli Haat INA");
        g.addStationToLine("Yellow", "Hauz Khas");
        g.addStationToLine("Yellow", "HUDA City Centre");

        // Red Line
        g.addEdge("Rithala", "Netaji Subhash Place", 5, 10);
        g.addEdge("Netaji Subhash Place", "Inderlok", 4, 6);
        g.addEdge("Inderlok", "Kashmere Gate", 6, 10);
        g.addEdge("Kashmere Gate", "Welcome", 7, 8);
        g.addEdge("Welcome", "New Bus Adda", 15, 24);
        g.addStationToLine("Red", "Rithala");
        g.addStationToLine("Red", "Netaji Subhash Place");
        g.addStationToLine("Red", "Inderlok");
        g.addStationToLine("Red", "Kashmere Gate");
        g.addStationToLine("Red", "Welcome");
        g.addStationToLine("Red", "New Bus Adda");

        // Green Line
        g.addEdge("Bahadurgarh City Park", "Punjabi Bagh West", 22, 38);
        g.addEdge("Punjabi Bagh West", "Ashok Park Main", 2, 4);
        g.addEdge("Ashok Park Main", "Inderlok", 2, 3);
        g.addEdge("Ashok Park Main", "Kirti Nagar", 3, 5);
        g.addStationToLine("Green", "Bahadurgarh City Park");
        g.addStationToLine("Green", "Punjabi Bagh West");
        g.addStationToLine("Green", "Ashok Park Main");
        g.addStationToLine("Green", "Inderlok");
        g.addStationToLine("Green", "Kirti Nagar");

        // Violet Line
        g.addEdge("Ballabhgarh", "Kalkaji Mandir", 26, 44);
        g.addEdge("Kalkaji Mandir", "Lajpat Nagar", 4, 8);
        g.addEdge("Lajpat Nagar", "Central Secretariat", 8, 11);
        g.addEdge("Central Secretariat", "Mandi House", 3, 5);
        g.addEdge("Mandi House", "Kashmere Gate", 6, 10);
        g.addStationToLine("Violet", "Ballabhgarh");
        g.addStationToLine("Violet", "Kalkaji Mandir");
        g.addStationToLine("Violet", "Lajpat Nagar");
        g.addStationToLine("Violet", "Central Secretariat");
        g.addStationToLine("Violet", "Mandi House");
        g.addStationToLine("Violet", "Kashmere Gate");

        // Orange Line
        g.addEdge("New Delhi", "IGI Airport T3", 20, 19);
        g.addEdge("IGI Airport T3", "Dwarka Sector 21", 3, 3);
        g.addStationToLine("Orange", "New Delhi");
        g.addStationToLine("Orange", "IGI Airport T3");
        g.addStationToLine("Orange", "Dwarka Sector 21");

        // Pink Line
        g.addEdge("Shiv Vihar", "Welcome", 8, 12);
        g.addEdge("Weclome", "Anand Vihar", 6, 10);
        g.addEdge("Anand Vihar", "Mayur Vihar Phase 1", 7, 16);
        g.addEdge("Mayur Vihar Phase 1", "Hazrat Nizamuddin", 5, 5);
        g.addEdge("Hazrat Nizamuddin", "Lajpat Nagar", 5, 9);
        g.addEdge("Lajpat Nagar", "Dilli Haat INA", 4, 5);
        g.addEdge("Dilli Haat INA", "Rajouri Garden", 17, 23);
        g.addEdge("Rajouri Garden", "Punjabi Bagh West", 4, 5);
        g.addEdge("Punjabi Bagh West", "Netaji Subhash Place", 4, 6);
        g.addEdge("Netaji Subhash Place", "Azadpur", 5, 5);
        g.addEdge("Azadpur", "Majlis Park", 3, 4);
        g.addStationToLine("Pink", "Shiv Vihar");
        g.addStationToLine("Pink", "Weclome");
        g.addStationToLine("Pink", "Anand Vihar");
        g.addStationToLine("Pink", "Mayur Vihar Phase 1");
        g.addStationToLine("Pink", "Hazrat Nizamuddin");
        g.addStationToLine("Pink", "Lajpat Nagar");
        g.addStationToLine("Pink", "Dilli Haat INA");
        g.addStationToLine("Pink", "Rajouri Garden");
        g.addStationToLine("Pink", "Punjabi Bagh West");
        g.addStationToLine("Pink", "Netaji Subhash Place");
        g.addStationToLine("Pink", "Azadpur");
        g.addStationToLine("Pink", "Majlis Park");

        // Magenta Line
        g.addEdge("Janakpuri West", "Hauz Khas", 20, 33);
        g.addEdge("Hauz Khas", "Kalkaji Mandir", 6, 11);
        g.addEdge("Kalkaji Mandir", "Botanical Garden", 13, 25);
        g.addStationToLine("Magenta", "Janakpuri West");
        g.addStationToLine("Magenta", "Hauz Khas");
        g.addStationToLine("Magenta", "Kalkaji Mandir");
        g.addStationToLine("Magenta", "Botanical Garden");

        // Grey Line
        g.addEdge("Dwarka", "Dhansa Bus Stand", 6, 11);
        g.addStationToLine("Grey", "Dwarka");
        g.addStationToLine("Grey", "Dhansa Bus Stand");
    }

    public static void main(String[] args) throws Exception {
        Main g = new Main();
        CreateMap(g);

        BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\t\t\t\tList Of Actions\n\n");
            System.out.println("1. Get the list of stations");
            System.out.println("2. Get the metro map");
            System.out.println(
                    "3. Get the shortest distance, time and path from a source station to a destination station");
            System.out.println("4. Exit the menu");
            System.out.print("\nEnter your choice (1 to 4) : ");
            int choice = Integer.parseInt(sc.readLine());
            if (choice == 4) {
                System.exit(0);
            }

            switch (choice) {
                case 1:
                    g.displayStations();
                    break;
                case 2:
                    g.displayMap();
                    break;

                case 3:
                    System.out.println("\n***********************************************************************\n");
                    System.out.println("\nEnter source station: ");
                    String src = sc.readLine();
                    System.out.println("\nEnter destination station: ");
                    String dest = sc.readLine();

                    HashMap<String, Boolean> processed = new HashMap<>();
                    if (!g.containsVertex(src) || !g.containsVertex(dest) || !g.hasPath(src, dest, processed)) {
                        System.out.println("Inputs are invalid!\n");
                    } else {
                        System.out.println("\nSource: " + src);
                        System.out.println("\nDestination: " + dest);
                        System.out.println("\nTotal Distance: " + g.dijkstra(src, dest, false) + " km");
                        System.out.println("\nTotal Time: " + g.dijkstra(src, dest, true) + " minutes");
                    }
                    System.out.println("\n***********************************************************************\n");
                    break;
                default:
                    System.out.println("Please enter a valid option!");
            }
        }
    }
}