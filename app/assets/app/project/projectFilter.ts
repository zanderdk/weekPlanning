import {Pipe, PipeTransform} from "@angular/core"
import {ProjectWrapper} from "../services/projectClasses";

@Pipe({ name: "projectFilter" })
export class ProjectFilter implements PipeTransform {
  transform(projects: ProjectWrapper[], args: String) {
    return projects.filter(pro => pro[0].name.toLowerCase().indexOf(args.toLowerCase()) !== -1);
  }
}