import {Pipe, PipeTransform} from "@angular/core"
import {WorkType} from "../services/workTypeClasses";

@Pipe({ name: "workTypeFilter" })
export class WorkTypeFilter implements PipeTransform {
  transform(works: WorkType[], args: String) {
    return works.filter(work => work.name.toLowerCase().indexOf(args.toLowerCase()) !== -1);
  }
}