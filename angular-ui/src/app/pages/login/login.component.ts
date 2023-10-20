import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Data, Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { UserAuthService } from 'src/app/_services/user-auth.service';
import { UserService } from 'src/app/_services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(
    private userService: UserService,
    private auth: UserAuthService,
    private router: Router,
    private toast: NgToastService,
  ) {}

  ngOnInit(): void {}

  login(loginForm: NgForm) {
    this.userService.login(loginForm.value).subscribe(
      (response: Data) => {
        this.auth.storeToken(response['token']);
          this.auth.storeRefreshToken(response['refreshToken']);
        
        this.router.navigate(['header']);
      },
      (error) => {
        console.log(error);
      }
    );
  }

  registerUser() {
    this.router.navigate(['/register']);
  }

}
