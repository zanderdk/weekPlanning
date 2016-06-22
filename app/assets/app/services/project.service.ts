import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { ProjectWrapper } from "./projectClasses"


@Injectable()
export class ProjectService {
    constructor(@Inject(Http) private http:Http) { }

    private projectsUrl = "/getProjectList"
    private addUrl = "/addProject"

    getProjects(): Promise<ProjectWrapper[]> {
        return this.http.get(this.projectsUrl)
          .toPromise()
          .then(this.extractData)
    }

    addProject(projectName: string): Promise<string> {
        return this.http
               .post(this.addUrl, {'projectName': projectName })
               .toPromise().then(res => res.text())
  }


    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }

}
