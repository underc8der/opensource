package opensource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ProjectsReader {
	
	private static final String INPUT = "src/projects.txt";
	private static final String OUTPUT = "src/summary.txt";
	private static final String IS_PROJECT = "([A-Z]+\\s*)+";
	
	private SortedMap<String, Set<String>> projects = new TreeMap<>();
	
	public List<String> readProjects() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(INPUT));
		List<String> result = reader.lines().collect(Collectors.toList());
		reader.close();
		return result;
	}
	
	private void summarizeProjects(List<String> data) {
		List<String> keys = data.stream()
				.filter(rec -> rec.matches(IS_PROJECT))
				.collect(Collectors.toList());
		
		Map<String, Set<String>> projectsXUserid = new TreeMap<>();
		IntStream.range(0, keys.size()).forEach(i -> {
			projectsXUserid.put(keys.get(i), 
				new HashSet<>(data.subList(data.indexOf(keys.get(i)) + 1, 
					keys.size() > (i + 1) ? data.indexOf(keys.get((i + 1))) : data.size())));
		});
		
		Map<String, Long> usersidCounter = projectsXUserid.values().stream()
				.flatMap(x -> x.stream())
				.collect(Collectors.groupingBy(k -> k, Collectors.counting()));
		
		List<String> noDuplicated = usersidCounter.entrySet().stream()
				.filter(map -> map.getValue() <= 1)
				.map(map -> map.getKey())
				.collect(Collectors.toList());
		
		projectsXUserid.values().stream().forEach(usersid -> {
			usersid.retainAll(noDuplicated);
		});
		
//		Map<String, Long> summary = projectsXUserid.values().stream()
//				.flatMap(x -> x.stream())
//				.collect(Collectors.groupingBy(k -> k, Collectors.counting()));
		
		System.out.println("shit" + sortProjects(projectsXUserid));
	}
	
	private List<Map.Entry<String, Set<String>>> sortProjects(Map<String, Set<String>> summary) {
		List<Map.Entry<String, Set<String>>> sorted =
                new LinkedList<Map.Entry<String, Set<String>>>(summary.entrySet());
		
		Collections.sort(sorted, new Comparator<Map.Entry<String, Set<String>>>() {
			@Override
			public int compare(Entry<String, Set<String>> o1, Entry<String, Set<String>> o2) {
				return Long.valueOf(o2.getValue().size()).compareTo(Long.valueOf(o1.getValue().size()));
			}
		});
		
		return sorted
				//.stream().collect( Collectors.toMap(Function.identity(), x -> x))
				;
	}
	
	private void writeFile() throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT));
		projects.forEach((k, v) -> {
			pw.println(k + " " + v.size());
		});
    	pw.close();
	}
	
	public static void main(String[] args) throws IOException {
		ProjectsReader reader = new ProjectsReader();
		reader.summarizeProjects(reader.readProjects());
		reader.writeFile();
		System.out.println(reader.getProjects());
	}

	public SortedMap<String, Set<String>> getProjects() {
		return projects;
	}

	public void setProjects(SortedMap<String, Set<String>> projects) {
		this.projects = projects;
	}
}
