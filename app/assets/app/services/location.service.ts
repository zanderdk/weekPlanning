import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { Location } from "./locationClasses"

@Injectable()
export class LocationService {

    private updateLocationUrl = "/updateLocation"
    private addLocationUrl = "/addLocation"
    private getLocationsUrl = "/getLocations"
    private getLocationUrl = "/getLocation"
    private deleteLocationUrl = "/deleteLocation"

    constructor(@Inject(Http) private http:Http) { }

    public addLocation(location: Location): Promise<string> {
        let json = JSON.stringify(location)
        return this.http.get(this.addLocationUrl + "?json=" + json).toPromise()
            .then(res => res.text())
    }

    public updateLocation(location: Location): Promise<string> {
        let json = JSON.stringify(location)
        return this.http.get(this.updateLocationUrl + "?json=" + json).toPromise()
            .then(res => res.text())
    }

    public deleteLocation(projectId: number, locationId: number): Promise<string> {
        return this.http.get(this.deleteLocationUrl + "?projectId=" + projectId + "&locationId=" + locationId)
            .toPromise()
            .then(res => res.text())
    }

    public getLocations(projectId: number): Promise<Location[]> {
        return this.http.get(this.getLocationsUrl + "?projectId=" + projectId).toPromise()
            .then(this.extractData)
    }

    public getLocation(projectId: number, locationId: number): Promise<Location> {
        return this.http.get(this.getLocationUrl + "?projectId=" + projectId + "&locationId=" + locationId)
            .toPromise().then(this.extractData)
    }

    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }
}
