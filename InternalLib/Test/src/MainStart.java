import java.io.IOException;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;


public class MainStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token("242613366a410ff2d7c160bf199bcf3cce3e6899");
		RepositoryService service = new RepositoryService(client);
		CommitService commitService = new CommitService(client);
		GitHubRequest tmp = new GitHubRequest();
		try {
			for (Repository repo : service.getRepositories("xuugii")){
				
			  System.out.println(repo.getName() + " String: " );
			  if (repo.getName().equals("AgileAndroid")){
				  for (int i = 0; i < commitService.getCommits(repo).size(); i++) {
				  System.out.println(commitService.getCommits(repo).get(i).getCommit().getMessage());
				}
			  }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
