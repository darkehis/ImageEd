#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)






void root (const uchar4* in,uchar4* out,uint32_t x,uint32_t y){

  const float4 inF = convert_float4(*in);
  float g = 0.3*inF.r + 0.59*inF.g + 0.11*inF.b;
  *out = convert_uchar4((float4){g,g,g,255});
  //*out =  rsPackColorTo8888(outF);


}
