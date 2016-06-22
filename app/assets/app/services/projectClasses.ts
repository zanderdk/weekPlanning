export class Project {
    constructor(
    public id: number,
    public name: string) {}
}

export type ProjectWrapper = [Project, string, string];
