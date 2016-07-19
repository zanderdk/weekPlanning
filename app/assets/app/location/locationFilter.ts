import {Pipe, PipeTransform} from "@angular/core"
import {Location} from "../services/locationClasses";

@Pipe({ name: "locationFilter" })
export class LocationFilter implements PipeTransform {
  transform(locations: Locations[], args: String) {
    return locations.filter(loc => loc.name.toLowerCase().indexOf(args.toLowerCase()) !== -1);
  }
}
