export class User {
    constructor(
    public username: string,
    public password: string,
    public email: string,
    public enabled: boolean,
    public admin: boolean
    ) {}
}

export type UserWrapper = [User, string];
