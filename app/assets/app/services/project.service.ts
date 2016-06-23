import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { ProjectWrapper } from "./projectClasses"


@Injectable()
export class ProjectService {
    constructor(@Inject(Http) private http:Http) { }

    private projectsUrl = "/getProjectList"
    private projectUrl = "/getProject"
    private addUrl = "/addProject"
    private deleteUrl = "/deleteProject"
    private updateUrl = "/updateProject"

    getProjects(): Promise<ProjectWrapper[]> {
        return this.http.get(this.projectsUrl)
          .toPromise()
          .then(this.extractData)
    }
    
    delete(id: number): Promise<string> {
        return this.http
            .get(this.deleteUrl+"?id="+id)
            .toPromise().then(res => res.text())
    }
    
    updateProject(pro: Project): Promise<string> {
        return this.http
            .post(this.updateUrl, {'id': pro.id, 'name': pro.name})
            .toPromise().then(res => res.text())
    }

    addProject(projectName: string): Promise<string> {
        return this.http
               .post(this.addUrl, {'projectName': projectName })
               .toPromise().then(res => res.text())
    }
    
    getProject(id: number): Promise<Project> {
        return this.http
            .get(this.projectUrl+"?id="+id)
            .toPromise().then(this.extractData)
    }

    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }

}
