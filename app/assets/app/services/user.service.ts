import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { User } from "./userClasses.ts"


@Injectable()
export class UserService {
    
    private getUserUrl = "/getUserById"
    private getViabilityUrl = "/getViability"
    
    constructor(@Inject(Http) private http:Http) {
    }
    
    getUsersVisabilityForProject(id: number): Promise<string> {
        return this.http.get(this.getViabilityUrl+"?id="+id).toPromise()
            .then(res => res.text())
    }

    getUserById(id: number): Promise<User[]> {
        return this.http.get(this.getUserUrl+"?id="+id).toPromise()
          .then(this.extractData)
    }
    
    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }
}