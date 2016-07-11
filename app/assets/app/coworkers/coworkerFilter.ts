import {Pipe, PipeTransform} from "@angular/core"
import {Coworker} from "../services/coworkerClasses";

@Pipe({ name: "coworkerFilter" })
export class CoworkerFilter implements PipeTransform {
  transform(workers: Coworker[], args: String) {
    return workers.filter(worker => worker.name.toLowerCase().indexOf(args.toLowerCase()) !== -1);
  }
}