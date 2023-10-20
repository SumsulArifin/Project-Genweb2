import { Component, OnInit } from '@angular/core';
import { FormGroup, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from 'src/app/_services/user.service';
import { User } from 'src/app/models/user.model';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {


user =new User("","","","");
file!:any

  constructor(private userService: UserService,
    private router: Router) { }

  ngOnInit(): void {
  }
  onChangeFileField(event:any){
    
    this.file=event.target.files[0]
    this.user.imageName = this.file.name;
    console.log(this.file);
    
  }
  doRegister(){
    this.userService.registerUser(this.user,this.file).subscribe({
      next:(response)=>{
        this.router.navigate(['/login']);
      },
      error:(error)=>{},
      complete:()=>{
        
      }

    })
  }

}
