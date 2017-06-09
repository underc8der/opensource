package opensource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ProjectsReader {
	
	private static final String INPUT = "src/projects.txt";
	private static final String OUTPUT = "src/summary.txt";
	private static final String IS_PROJECT = "([A-Z]+\\s*)+";
	
	private List<Map.Entry<String, Set<String>>> summary;
	
	private List<String> readProjectsFile() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(INPUT));
		List<String> result = reader.lines().collect(Collectors.toList());
		reader.close();
		return result;
	}
	
	public void summarizeProjects() throws IOException {
		List<String> data = readProjectsFile();
		
		List<String> projects = data.stream()
				.filter(rec -> rec.matches(IS_PROJECT))
				.collect(Collectors.toList());
		
		Map<String, Set<String>> projectsXUID = new TreeMap<>();
		IntStream.range(0, projects.size()).forEach(i -> {
			projectsXUID.put(projects.get(i), 
				new HashSet<>(data.subList(data.indexOf(projects.get(i)) + 1, 
					projects.size() > (i + 1) ? data.indexOf(projects.get((i + 1))) : data.size())));
		});
		
		Map<String, Long> uidGrouped = projectsXUID.values().stream()
				.flatMap(uid -> uid.stream())
				.filter(uid -> !uid.equals("0") && !uid.equals("1"))
				.collect(Collectors.groupingBy(k -> k, Collectors.counting()));
		
		List<String> uidNonDuplicates = uidGrouped.entrySet().stream()
				.filter(uids -> uids.getValue() <= 1)
				.map(uids -> uids.getKey())
				.collect(Collectors.toList());
		
		projectsXUID.values().stream().forEach(usersid -> {
			usersid.retainAll(uidNonDuplicates);
		});
		
		summary = sortProjects(projectsXUID);
	}
	
	private List<Map.Entry<String, Set<String>>> sortProjects(Map<String, Set<String>> summary) {
		List<Map.Entry<String, Set<String>>> sortedProjects =
                new LinkedList<Map.Entry<String, Set<String>>>(summary.entrySet());
		
		Collections.sort(sortedProjects, new Comparator<Map.Entry<String, Set<String>>>() {
			@Override
			public int compare(Entry<String, Set<String>> o1, Entry<String, Set<String>> o2) {
				return Long.valueOf(o2.getValue().size()).compareTo(Long.valueOf(o1.getValue().size()));
			}
		});
		
		return sortedProjects;
	}
	
	private void writeFile() throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT));
		summary.stream().forEach(v -> {
			pw.println(v.getKey() + " " + v.getValue().size());
		});
    	pw.close();
	}
	
	public List<Map.Entry<String, Set<String>>> getProjects() {
		return summary;
	}

	public static void main(String[] args) throws IOException {
		ProjectsReader reader = new ProjectsReader();
		reader.summarizeProjects();
		reader.writeFile();
		System.out.println("done...");
	}
}
