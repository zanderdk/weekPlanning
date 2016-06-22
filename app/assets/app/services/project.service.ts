import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import {ProjectWrapper} from "./projectClasses"


@Injectable()
export class ProjectService {
    constructor(@Inject(Http) private http:Http) { }

    private projectsUrl = "/getProjectList"

    getProjects (): Promise<ProjectWrapper[]> {
      return this.http.get(this.projectsUrl)
          .toPromise()
          .then(this.extractData)
    }


    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }

}
