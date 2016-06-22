import { Injectable }     from "angular2/core"
import { Http, Response } from "angular2/http"
import { Headers, RequestOptions } from "angular2/http"
import {ProjectWrapper} from "./projectClasses"


@Injectable()
export class ProjectService {
    constructor(private http:Http) { }

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
