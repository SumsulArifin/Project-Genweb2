import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/user.model';
import { TokenApiModel } from '../models/token-api.model';


@Injectable({
  providedIn: 'root',
})
export class UserService {


  
  PATH_OF_API = 'http://localhost:8080';

  requestHeader = new HttpHeaders({ 'No-Auth': 'True' });
  constructor(
    private httpclient: HttpClient,
    // private userAuthService: UserAuthService
  ) {}


  registerUser(user: User) {
    return this.httpclient.post<User>(`${this.PATH_OF_API}/api/v1/auth/register`, user);
  }

  public login(loginData: any) {
    return this.httpclient.post(this.PATH_OF_API + '/api/v1/auth/authenticate', loginData, {
      headers: this.requestHeader,
    });
  }
  renewToken(tokenApi : TokenApiModel){
    return this.httpclient.post<any>(`${this.PATH_OF_API}refresh`, tokenApi)
  }

  getUsers() {
    return this.httpclient.get<any>(this.PATH_OF_API+'/api/auth/all-with-images');
  }
  public getProductDetailsById(id: number) {
    return this.httpclient.get<User>(this.PATH_OF_API+"/api/auth/image/"+id);
  }


}
