export class Project {
    constructor(
    public id: Number,
    public name: String) {}
}

export type ProjectWrapper = [Project, String, String];
