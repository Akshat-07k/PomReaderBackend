package com.project.dependency.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dependency.models.Dependency;

@Service
public class PomReadService {

    public List<Dependency> parsePomDependencies(MultipartFile file){

        List<Dependency> dependencies = new ArrayList<>();
        Map<String,String> propertiesMap = new HashMap<>();

        InputStream inputStream = null;

        try{
            inputStream = file.getInputStream();

            if(inputStream==null)throw new RuntimeException("File input stream is null");

            Document document = Jsoup.parse(inputStream,"UTF-8",""); // XML -> DOM Tree

            Element dependenciesElement = document.selectFirst("dependencies"); // <dependencies/>
            Element propertiesElement = document.selectFirst("properties");  //<properties/>

            if(propertiesElement != null){
                for(Element property : propertiesElement.children()){
                    propertiesMap.put(property.tagName(),property.text());
                }
            }

            if(dependenciesElement == null)return null;

            for(Element dependencyElement : dependenciesElement.select("dependency") ) // <dependency/>
            {

                Element groupIdElement = dependencyElement.selectFirst("groupId");
                String groupId = (groupIdElement != null)? groupIdElement.text() : "";

                Element artifactIdElement = dependencyElement.selectFirst("artifactId");
                String artifactId = (groupIdElement != null)? artifactIdElement.text() : "";

                Element versionElement = dependencyElement.selectFirst("version");
                String version = (versionElement != null) ? versionElement.text() : null;

                // Resolve property placeholders if present in the version
                if (version != null && version.startsWith("${") && version.endsWith("}")) {
                    String propertyName = version.substring(2, version.length() - 1); // Strip ${ and }
                    version = propertiesMap.getOrDefault(propertyName, version); // Resolve or keep original
                }
   

                dependencies.add(new Dependency(groupId, artifactId,version,getCurrentVersionFromApi(groupId, artifactId)));
            }
            
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        return dependencies;
    }



    private String getCurrentVersionFromApi(String groupId, String artifactId) {
        
        try {
            String encodedGroupId = URLEncoder.encode(groupId, "UTF-8");
            String encodedArtifactId = URLEncoder.encode(artifactId, "UTF-8");
            String apiUrl = "https://search.maven.org/solrsearch/select?q=g:" + encodedGroupId + "+AND+a:" + encodedArtifactId + "&rows=1&wt=json";
            
            // Make the HTTP GET request to the API
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // Timeout after 5 seconds
            connection.setReadTimeout(5000);

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the response to get the latest version
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            JsonNode docs = rootNode.path("response").path("docs");

            if (docs.isArray() && docs.size() > 0) {
                return docs.get(0).path("latestVersion").asText("N/a");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/a";  // Default value if the API call fails
    }
    
}
